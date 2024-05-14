package com.risky.monospace.service;

import com.risky.monospace.model.BluetoothStatus;
import com.risky.monospace.service.subscribers.BluetoothSubscriber;

public class BluetoothService extends MonoService<BluetoothSubscriber> {
    private static BluetoothService instance;
    private BluetoothStatus connectionStatus = BluetoothStatus.UNAVAILABLE;

    private BluetoothService() {
    }

    public static BluetoothService getInstance() {
        if (instance == null) {
            instance = new BluetoothService();
        }

        return instance;
    }

    public void set(BluetoothStatus status) {
        connectionStatus = status;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(BluetoothSubscriber subscriber) {
        subscriber.update(connectionStatus);
    }
}
