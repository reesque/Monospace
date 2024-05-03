package com.risky.monospace.util;

import com.risky.monospace.model.LocationStatus;

public interface LocationSubscriber {
    void update(LocationStatus status);
}
