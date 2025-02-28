package com.risky.monospace.model;

import androidx.annotation.Nullable;

public class Notification {
    public final int icon;
    public final String packageName;
    private final String key;

    public Notification(int icon, String packageName, String key) {
        this.icon = icon;
        this.packageName = packageName;
        this.key = key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Notification) {
            return this.key.equals(((Notification) obj).key);
        }

        return false;
    }
}
