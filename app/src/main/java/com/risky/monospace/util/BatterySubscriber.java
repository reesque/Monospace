package com.risky.monospace.util;

public interface BatterySubscriber {
    void update(int level, boolean isCharging, boolean isFull);
}
