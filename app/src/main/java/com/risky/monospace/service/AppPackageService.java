package com.risky.monospace.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.risky.monospace.model.AppPackage;
import com.risky.monospace.util.PacManSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppPackageService {
    private static final List<AppPackage> appList = new ArrayList<>();
    private static PacManSubscriber subscriber;

    public static void refresh(Context context) {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);


        appList.clear();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null){
                try {
                    Drawable icon = pm.getApplicationIcon(installedApps.get(i).packageName);
                    appList.add(new AppPackage(installedApps.get(i).packageName,
                            (String) pm.getApplicationLabel(installedApps.get(i)), icon));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        appList.sort(new AppPackage.ItemComparator());
        notifySubscriber();
    }

    public static void subscribe(PacManSubscriber sub) {
        subscriber = sub;
        notifySubscriber();
    }

    public static void notifySubscriber() {
        if (subscriber != null) {
            subscriber.update(Collections.unmodifiableList(appList));
        }
    }
}
