package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;

public interface AirpodSubscriber extends MonoSubscriber {
    void update(SinglePod pod);

    void update(RegularPod pod);
}
