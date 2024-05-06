package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.BluetoothStatus;

public interface BluetoothSubscriber extends MonoSubscriber {
    void update(BluetoothStatus status);
}
