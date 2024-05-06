package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.Notification;

import java.util.List;

public interface NotificationSubscriber extends MonoSubscriber {
    void update(List<Notification> notifications);
}
