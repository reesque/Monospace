package com.risky.monospace.util;

import com.risky.monospace.model.BluetoothStatus;

public interface BluetoothSubscriber {
    void update(BluetoothStatus status);
}
