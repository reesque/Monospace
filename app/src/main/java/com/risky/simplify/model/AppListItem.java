package com.risky.simplify.model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class AppListItem {
    public final String packageName;
    public final String name;
    public final Drawable icon;

    public AppListItem(String packageName, String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public static class ItemComparator implements Comparator<AppListItem> {
        @Override
        public int compare(AppListItem o1, AppListItem o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}
