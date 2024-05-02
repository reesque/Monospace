package com.risky.monospace.model;

import androidx.annotation.Nullable;

public class Notification {
    public final int id;
    public final int icon;
    public final String packageName;

    public Notification(int id, int icon, String packageName) {
        this.id = id;
        this.icon = icon;
        this.packageName = packageName;
    }

    @Override
    public int hashCode() {
        return id + icon + packageName.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Notification) {
            return this.id == ((Notification) obj).id
                    && this.icon == ((Notification) obj).icon
                    && this.packageName.equals(((Notification) obj).packageName);
        }

        return false;
    }
}
