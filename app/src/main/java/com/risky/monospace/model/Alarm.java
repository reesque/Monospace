package com.risky.monospace.model;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Alarm {
    public final Calendar triggerTime;
    public final String packageName;

    public Alarm(Calendar triggerTime, String packageName) {
        this.triggerTime = triggerTime;
        this.packageName = packageName;
    }

    @NonNull
    @Override
    public Object clone() {
        return new Alarm((Calendar) triggerTime.clone(), packageName);
    }
}
