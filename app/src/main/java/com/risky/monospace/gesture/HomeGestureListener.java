package com.risky.monospace.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final GestureCallback searchCallback;
    private final GestureCallback calendarCallback;

    private float distX = 0f;
    private float distY = 0f;

    public HomeGestureListener(GestureCallback searchCallback,
                               GestureCallback calendarCallback) {
        this.searchCallback = searchCallback;
        this.calendarCallback = calendarCallback;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getAction() == MotionEvent.ACTION_DOWN && e2.getAction() == MotionEvent.ACTION_UP) {
            if (velocityX >= 100 && distY >= -30 && distY <= 30) {
                searchCallback.run();
                return true;
            }

            if (velocityX <= -100 && distY >= -30 && distY <= 30) {
                calendarCallback.run();
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        distX = distanceX;
        distY = distanceY;

        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
