package com.risky.monospace.dialog;

import android.view.Gravity;

public enum DialogType {
    MEDIA(false, false, Gravity.BOTTOM, 0.7f),
    AIRPOD(true, false, Gravity.BOTTOM, 0.7f),
    WEATHER(false, false, Gravity.BOTTOM, 0.7f),
    NOTIFICATION(false, false, Gravity.BOTTOM, 0.7f),
    SEARCH(false, false, Gravity.TOP, 0.95f),
    CALENDAR(false, false, Gravity.BOTTOM, 0.7f);

    public final boolean shouldDrawOver;
    public final boolean shouldClickThrough;
    public final int gravity;
    public final float dimAlpha;

    DialogType(boolean shouldDrawOver, boolean shouldClickThrough, int gravity, float dimAlpha) {
        this.shouldDrawOver = shouldDrawOver;
        this.shouldClickThrough = shouldClickThrough;
        this.gravity = gravity;
        this.dimAlpha = dimAlpha;
    }
}
