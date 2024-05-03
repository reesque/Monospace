package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.NetworkStatus;

public interface NetworkSubscriber {
    void update(NetworkStatus status);
}
