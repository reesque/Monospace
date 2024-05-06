package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.NetworkStatus;

public interface NetworkSubscriber extends MonoSubscriber {
    void update(NetworkStatus status);
}
