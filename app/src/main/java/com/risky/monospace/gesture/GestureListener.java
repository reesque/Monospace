package com.risky.monospace.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private final GestureCallback doubleTapCallback;

    public GestureListener(GestureCallback doubleTapCallback) {
        this.doubleTapCallback = doubleTapCallback;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        doubleTapCallback.run();

        return true;
    }
}
