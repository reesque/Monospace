package com.risky.monospace.dialog;

public enum DialogType {
    MEDIA(false),
    AIRPOD(true),
    WEATHER(false),
    NOTIFICATION(false),
    SEARCH(false),
    CALENDAR(false);

    private final boolean shouldDrawOver;

    DialogType(boolean shouldDrawOver) {
        this.shouldDrawOver = shouldDrawOver;
    }

    public boolean shouldDrawOver() {
        return shouldDrawOver;
    }
}
