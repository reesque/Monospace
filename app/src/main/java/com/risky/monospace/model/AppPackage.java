package com.risky.monospace.model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class AppPackage {
    public final String packageName;
    public final String name;
    public final Drawable icon;

    public AppPackage(String packageName, String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
    }

    public static class ItemComparator implements Comparator<AppPackage> {
        @Override
        public int compare(AppPackage o1, AppPackage o2) {
            return o1.name.compareTo(o2.name);
        }
    }
}
