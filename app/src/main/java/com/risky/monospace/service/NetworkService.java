package com.risky.monospace.service;

import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.service.subscribers.NetworkSubscriber;

public class NetworkService extends MonoService<NetworkSubscriber> {
    private static NetworkService instance;
    private NetworkStatus networkStatus = NetworkStatus.UNAVAILABLE;

    private NetworkService() {}

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }

        return instance;
    }

    public void set(NetworkStatus status) {
        networkStatus = status;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(NetworkSubscriber subscriber) {
        subscriber.update(networkStatus);
    }
}
