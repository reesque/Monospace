package com.risky.monospace.model;

public abstract class Pod {
    public final AirpodModel model;
    public final boolean isDisconnected;
    public final boolean isSingle;

    public Pod(String model, boolean isDisconnected, boolean isSingle) {
        this.model = AirpodModel.get(model);
        this.isDisconnected = isDisconnected;
        this.isSingle = isSingle;
    }
}
