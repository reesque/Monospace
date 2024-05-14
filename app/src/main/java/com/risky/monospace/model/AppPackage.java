package com.risky.monospace.model;

import android.graphics.drawable.Drawable;

import java.util.Comparator;

public class AppPackage {
    public final String packageName;
    public final String name;
    public final Drawable icon;
    private boolean isFav;

    public AppPackage(String packageName, String name, Drawable icon, boolean isFav) {
        this.name = name;
        this.icon = icon;
        this.packageName = packageName;
        this.isFav = isFav;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean isFav) {
        this.isFav = isFav;
    }

    public static class ItemComparator implements Comparator<AppPackage> {
        @Override
        public int compare(AppPackage o1, AppPackage o2) {
            if (o1.isFav && !o2.isFav) {
                return -1;
            } else if (!o1.isFav && o2.isFav) {
                return 1;
            }

            return o1.name.compareTo(o2.name);
        }
    }
}
