package com.risky.monospace.util;

import com.risky.monospace.model.Notification;

import java.util.List;

public interface NotificationSubscriber {
    void update(List<Notification> notifications);
}
