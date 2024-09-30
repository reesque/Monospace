package com.risky.monospace.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final GestureCallback searchCallback;

    public HomeGestureListener(GestureCallback searchCallback) {
        this.searchCallback = searchCallback;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        super.onLongPress(e);

        searchCallback.run();
    }
}
