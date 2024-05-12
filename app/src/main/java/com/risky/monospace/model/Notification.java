package com.risky.monospace.model;

import android.app.PendingIntent;

import androidx.annotation.Nullable;

import com.risky.monospace.service.NotificationService;

public class Notification {
    public final int id;
    public final int icon;
    public final String packageName;
    public final String title;
    public final String description;
    private final PendingIntent clickAction;
    private final PendingIntent clearAction;

    public Notification(int id, int icon, String packageName, String title,
                        String description, PendingIntent clickAction, PendingIntent clearAction) {
        this.id = id;
        this.icon = icon;
        this.packageName = packageName;
        this.title = title;
        this.description = description;
        this.clickAction = clickAction;
        this.clearAction = clearAction;
    }

    @Override
    public int hashCode() {
        return id + icon + packageName.hashCode()
                + (title == null ? "" : title).hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Notification) {
            String thisTitle = (title == null ? "" : title);
            String objTitle = (((Notification) obj).title == null ? "" : ((Notification) obj).title);


            return this.id == ((Notification) obj).id
                    && this.icon == ((Notification) obj).icon
                    && this.packageName.equals(((Notification) obj).packageName)
                    && thisTitle.equals(objTitle);
        }

        return false;
    }

    public void onClick() {
        try {
            clickAction.send();
            clearAction.send();
        } catch (PendingIntent.CanceledException e) {
            // This indicates when the notification was already cleared, no need to handle
        }

        NotificationService.getInstance().remove(this);
    }
}
