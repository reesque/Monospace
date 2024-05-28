package com.risky.monospace.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.risky.monospace.R;
import com.risky.monospace.model.AppPackage;
import com.risky.monospace.service.subscribers.AppPackageSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppPackageService extends MonoService<AppPackageSubscriber> {
    private static AppPackageService instance;
    private final List<AppPackage> appList = new ArrayList<>();
    private Set<String> favList = new HashSet<>();

    private AppPackageService() {
    }

    public static AppPackageService getInstance(Context context) {
        if (instance == null) {
            instance = new AppPackageService();

            SharedPreferences sharedPref = context.getSharedPreferences("apps", MODE_PRIVATE);
            Set<String> cachedFav = sharedPref.getStringSet("favorite_app", null);
            if (cachedFav != null && cachedFav.size() != 0) {
                instance.favList.addAll(cachedFav);
            }
        }

        return instance;
    }

    public void refresh(Context context) {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);

        appList.clear();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null) {
                try {
                    Drawable icon = pm.getApplicationIcon(installedApps.get(i).packageName);
                    String name = installedApps.get(i).packageName;
                    appList.add(new AppPackage(name, (String) pm.getApplicationLabel(
                            installedApps.get(i)), icon, favList.contains(name)));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        appList.sort(new AppPackage.ItemComparator());
        notifySubscriber();
    }

    public void toggleFav(Context context, AppPackage appPackage) {
        String toastMsg = "";
        if (favList.contains(appPackage.packageName)) {
            favList.remove(appPackage.packageName);
            toastMsg = String.format("%s %s %s",
                    context.getText(R.string.toast_remove), appPackage.name,
                    context.getText(R.string.toast_from_fav));
        } else {
            favList.add(appPackage.packageName);
            toastMsg = String.format("%s %s %s",
                    context.getText(R.string.toast_add), appPackage.name,
                    context.getText(R.string.toast_to_fav));
        }

        refresh(context);
        notifySubscriber();
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show();

        // Do this in the background while front ground is updated with the change
        context.getSharedPreferences("apps", MODE_PRIVATE)
                .edit().putStringSet("favorite_app", favList).apply();
    }

    @Override
    protected void updateSubscriber(AppPackageSubscriber subscriber) {
        subscriber.update(Collections.unmodifiableList(appList));
    }
}
