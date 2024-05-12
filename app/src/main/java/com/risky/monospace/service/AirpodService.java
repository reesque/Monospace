package com.risky.monospace.service;

import android.content.Context;

import com.risky.monospace.dialog.DialogType;
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

    public void set(Context context, SinglePod pod) {
        Pod lastUpdated = this.pod;

        this.pod = pod;
        notifySubscriber();

        if ((lastUpdated == null || lastUpdated.isDisconnected) && pod != null && !pod.isDisconnected) {
            DialogService.getInstance().show(context, DialogType.AIRPOD);
        }
    }

    public void set(Context context, RegularPod pod) {
        Pod lastUpdated = this.pod;

        this.pod = pod;
        notifySubscriber();

        if ((lastUpdated == null || lastUpdated.isDisconnected) && pod != null && !pod.isDisconnected) {
            DialogService.getInstance().show(context, DialogType.AIRPOD);
        }
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
