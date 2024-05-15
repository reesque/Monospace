package com.risky.monospace.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final GestureCallback drawerCallback;
    private final GestureCallback searchCallback;

    public HomeGestureListener(GestureCallback scrollCallback, GestureCallback searchCallback) {
        this.drawerCallback = scrollCallback;
        this.searchCallback = searchCallback;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getAction() == MotionEvent.ACTION_DOWN && e2.getAction() == MotionEvent.ACTION_UP) {
            if (velocityY <= -100) {
                drawerCallback.run();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        searchCallback.run();

        return true;
    }
}
