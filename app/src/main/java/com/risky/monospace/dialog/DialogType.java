package com.risky.monospace.dialog;

import android.view.Gravity;

public enum DialogType {
    MEDIA(false, Gravity.BOTTOM, 0.7f),
    AIRPOD(true, Gravity.BOTTOM, 0.7f),
    WEATHER(false, Gravity.BOTTOM, 0.7f),
    NOTIFICATION(false, Gravity.BOTTOM, 0.7f),
    SEARCH(false, Gravity.TOP, 0.95f),
    CALENDAR(false, Gravity.BOTTOM, 0.7f);

    public final boolean shouldDrawOver;
    public final int gravity;
    public final float dimAlpha;

    DialogType(boolean shouldDrawOver, int gravity, float dimAlpha) {
        this.shouldDrawOver = shouldDrawOver;
        this.gravity = gravity;
        this.dimAlpha = dimAlpha;
    }
}
