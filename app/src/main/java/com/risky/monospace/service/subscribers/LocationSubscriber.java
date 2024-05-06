package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.LocationStatus;

public interface LocationSubscriber extends MonoSubscriber {
    void update(LocationStatus status);
}
