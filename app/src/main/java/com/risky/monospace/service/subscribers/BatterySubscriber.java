package com.risky.monospace.service.subscribers;

public interface BatterySubscriber {
    void update(int level, boolean isCharging, boolean isFull);
}
