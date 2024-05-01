package com.risky.simplify.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.risky.simplify.model.AppListItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppCacheService {
    private static final List<AppListItem> appList = new ArrayList<>();

    public static void refresh(Context context) {
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> installedApps =
                pm.getInstalledApplications(PackageManager.PERMISSION_GRANTED);


        appList.clear();
        for (int i = 0; i < installedApps.size(); i++) {
            if (pm.getLaunchIntentForPackage(installedApps.get(i).packageName) != null){
                try {
                    Drawable icon = pm.getApplicationIcon(installedApps.get(i).packageName);
                    appList.add(new AppListItem(installedApps.get(i).packageName,
                            (String) pm.getApplicationLabel(installedApps.get(i)), icon));
                } catch (PackageManager.NameNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        appList.sort(new AppListItem.ItemComparator());
    }

    public static List<AppListItem> get() {
        return Collections.unmodifiableList(appList);
    }
}
