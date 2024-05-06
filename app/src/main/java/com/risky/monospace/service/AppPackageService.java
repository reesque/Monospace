package com.risky.monospace.service;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

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
    private final Set<String> favList = new HashSet<>();

    private AppPackageService() {}

    public static AppPackageService getInstance() {
        if (instance == null) {
            instance = new AppPackageService();
        }

        return instance;
    }

    public void refresh(Context context) {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);

        SharedPreferences sharedPref = context.getSharedPreferences("apps", MODE_PRIVATE);
        Set<String> storedFavList = sharedPref.getStringSet("favorite_app", null);

        favList.clear();
        if (storedFavList != null) {
            favList.addAll(storedFavList);
        }

        appList.clear();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null){
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

    public void toggleFav(Context context, String packageName) {
        if (favList.contains(packageName)) {
            favList.remove(packageName);
        } else {
            favList.add(packageName);
        }

        context.getSharedPreferences("apps", MODE_PRIVATE)
                .edit().putStringSet("favorite_app", favList).apply();

        refresh(context);
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(AppPackageSubscriber subscriber) {
        subscriber.update(Collections.unmodifiableList(appList));
    }
}
