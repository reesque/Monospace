package com.risky.monospace.dialog;

import android.view.Gravity;

import com.risky.monospace.R;

public enum DialogType {
    MEDIA(false, false, Gravity.BOTTOM, 0.7f, R.style.MonoDialogSlideBottom),
    AIRPOD(true, false, Gravity.BOTTOM, 0.7f, R.style.MonoDialogSlideBottom),
    WEATHER(false, false, Gravity.BOTTOM, 0.7f, R.style.MonoDialogSlideBottom),
    SEARCH(false, false, Gravity.TOP, 0.95f, R.style.MonoDialogFade),
    CALENDAR(false, true, Gravity.TOP, 0.95f, R.style.MonoDialogFade);

    public final boolean shouldDrawOver;
    public final boolean shouldFullscreen;
    public final int gravity;
    public final float dimAlpha;
    public final int theme;

    DialogType(boolean shouldDrawOver, boolean shouldFullscreen, int gravity, float dimAlpha, int theme) {
        this.shouldDrawOver = shouldDrawOver;
        this.shouldFullscreen = shouldFullscreen;
        this.gravity = gravity;
        this.dimAlpha = dimAlpha;
        this.theme = theme;
    }
}
