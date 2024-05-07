package com.risky.monospace.service;

import com.risky.monospace.model.Pod;
import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;
import com.risky.monospace.service.subscribers.AirpodSubcriber;

public class AirpodService extends MonoService<AirpodSubcriber> {
    private static AirpodService instance;
    private Pod pod;

    private AirpodService() {}

    public static AirpodService getInstance() {
        if (instance == null) {
            instance = new AirpodService();
        }

        return instance;
    }

    public void set(SinglePod pod) {
        this.pod = pod;
        notifySubscriber();
    }

    public void set(RegularPod pod) {
        this.pod = pod;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(AirpodSubcriber subscriber) {
        if (pod == null) {
            subscriber.update((SinglePod) null);
            return;
        }
        
        if (pod.isSingle) {
            subscriber.update((SinglePod) pod);
        } else {
            subscriber.update((RegularPod) pod);
        }
    }
}
