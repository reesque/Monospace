package com.risky.monospace.receiver;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.risky.monospace.model.Notification;
import com.risky.monospace.service.NotificationService;

public class NotificationReceiver extends NotificationListenerService {
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        for (StatusBarNotification sbn : getActiveNotifications()) {
            NotificationService.add(new Notification(sbn.getId(),
                    sbn.getNotification().getSmallIcon().getResId(),
                    sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        NotificationService.add(new Notification(sbn.getId(),
                sbn.getNotification().getSmallIcon().getResId(),
                sbn.getPackageName()));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        NotificationService.remove(new Notification(sbn.getId(),
                sbn.getNotification().getSmallIcon().getResId(),
                sbn.getPackageName()));
    }
}
