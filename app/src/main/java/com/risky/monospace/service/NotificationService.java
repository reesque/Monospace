package com.risky.monospace.service;

import com.risky.monospace.model.Notification;
import com.risky.monospace.service.subscribers.NotificationSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationService extends MonoService<NotificationSubscriber> {
    private static NotificationService instance;
    private final List<Notification> notificationList = new ArrayList<>();

    private NotificationService() {
    }

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }

        return instance;
    }

    public void add(Notification notification) {
        if (!notificationList.contains(notification)) {
            notificationList.add(notification);
            notifySubscriber();
        }
    }

    public void remove(Notification notification) {
        notificationList.remove(notification);
        notifySubscriber();
    }

    public void removeAll() {
        notificationList.clear();
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(NotificationSubscriber subscriber) {
        subscriber.update(Collections.unmodifiableList(notificationList));
    }
}
