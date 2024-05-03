package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.BluetoothStatus;

public interface BluetoothSubscriber {
    void update(BluetoothStatus status);
}
