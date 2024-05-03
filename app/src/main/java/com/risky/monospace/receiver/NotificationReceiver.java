package com.risky.monospace.receiver;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.risky.monospace.model.Notification;
import com.risky.monospace.service.NotificationService;

public class NotificationReceiver extends NotificationListenerService {
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        for (StatusBarNotification sbn : getActiveNotifications()) {
            if (sbn.getNotification().category != null
                    && (sbn.getNotification().flags
                    & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
                NotificationService.add(new Notification(sbn.getId(),
                        sbn.getNotification().getSmallIcon().getResId(),
                        sbn.getPackageName()));
            }
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn.getNotification().category != null
                && (sbn.getNotification().flags
                & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
            NotificationService.add(new Notification(sbn.getId(),
                    sbn.getNotification().getSmallIcon().getResId(),
                    sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn.getNotification().category != null
                && (sbn.getNotification().flags
                & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
            NotificationService.remove(new Notification(sbn.getId(),
                    sbn.getNotification().getSmallIcon().getResId(),
                    sbn.getPackageName()));
        }
    }
}
