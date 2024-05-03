package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.LocationStatus;

public interface LocationSubscriber {
    void update(LocationStatus status);
}
