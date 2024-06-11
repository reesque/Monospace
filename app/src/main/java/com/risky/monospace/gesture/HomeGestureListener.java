package com.risky.monospace.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class HomeGestureListener extends GestureDetector.SimpleOnGestureListener {
    private final GestureCallback drawerCallback;
    private final GestureCallback searchCallback;
    private final GestureCallback settingsCallback;
    private final GestureCallback notificationCallback;
    private final GestureCallback calendarCallback;

    private float distX = 0f;
    private float distY = 0f;

    public HomeGestureListener(GestureCallback scrollCallback, GestureCallback searchCallback,
                               GestureCallback settingsCallback, GestureCallback notificationCallback,
                               GestureCallback calendarCallback) {
        this.drawerCallback = scrollCallback;
        this.searchCallback = searchCallback;
        this.settingsCallback = settingsCallback;
        this.notificationCallback = notificationCallback;
        this.calendarCallback = calendarCallback;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getAction() == MotionEvent.ACTION_DOWN && e2.getAction() == MotionEvent.ACTION_UP) {
            if (velocityX >= 100 && distY >= -20 && distY <= 20) {
                notificationCallback.run();
                return true;
            }

            if (velocityX <= -100 && distY >= -20 && distY <= 20) {
                calendarCallback.run();
                return true;
            }

            if (velocityY <= -100 && distX >= -200 && distX <= 200) {
                drawerCallback.run();
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

    @Override
    public boolean onDoubleTap(@NonNull MotionEvent e) {
        searchCallback.run();

        return true;
    }

    @Override
    public void onLongPress(@NonNull MotionEvent e) {
        settingsCallback.run();
    }
}
