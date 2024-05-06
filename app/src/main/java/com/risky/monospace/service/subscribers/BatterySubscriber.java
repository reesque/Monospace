package com.risky.monospace.service.subscribers;

public interface BatterySubscriber extends MonoSubscriber {
    void update(int level, boolean isCharging, boolean isFull);
}
