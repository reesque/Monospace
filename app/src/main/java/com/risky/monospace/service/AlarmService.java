package com.risky.monospace.service;

import com.risky.monospace.service.subscribers.AlarmSubscriber;

import java.util.Calendar;

public class AlarmService extends MonoService<AlarmSubscriber> {
    private static AlarmService instance;
    private Calendar currentAlarm;

    private AlarmService() {}

    public static AlarmService getInstance() {
        if (instance == null) {
            instance = new AlarmService();
        }

        return instance;
    }

    public Calendar get() {
        return (Calendar) currentAlarm.clone();
    }

    public void set(Calendar nextAlarm) {
        currentAlarm = nextAlarm;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(AlarmSubscriber subscriber) {
        subscriber.update(currentAlarm);
    }
}
