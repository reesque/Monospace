package com.risky.monospace.service;

import com.risky.monospace.model.Notification;
import com.risky.monospace.service.subscribers.NotificationSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService {
    private static final List<Notification> notificationList = new ArrayList<>();
    private static NotificationSubscriber subscriber;

    public static void add(Notification notification) {
        if (!notificationList.contains(notification)) {
            notificationList.add(notification);
            notifySubscriber();
        }
    }

    public static void remove(Notification notification) {
        notificationList.remove(notification);
        notifySubscriber();
    }

    public static void subscribe(NotificationSubscriber sub) {
        subscriber = sub;
        notifySubscriber();
    }

    private static void notifySubscriber() {
        if (subscriber != null) {
            subscriber.update(Collections.unmodifiableList(notificationList));
        }
    }
}
