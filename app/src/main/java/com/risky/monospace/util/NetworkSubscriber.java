package com.risky.monospace.util;

import com.risky.monospace.model.NetworkStatus;

public interface NetworkSubscriber {
    void update(NetworkStatus status);
}
