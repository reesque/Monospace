package com.risky.monospace.model;

public class SinglePod extends Pod {
    public final String status;
    public final boolean isCharging;


    public SinglePod(String model, boolean isDisconnected, String status, boolean isCharging) {
        super(model, isDisconnected, true);

        this.status = status;
        this.isCharging = isCharging;
    }
}
