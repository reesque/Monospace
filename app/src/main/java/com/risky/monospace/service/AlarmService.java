package com.risky.monospace.service;

import com.risky.monospace.model.Alarm;
import com.risky.monospace.service.subscribers.AlarmSubscriber;

import java.util.Calendar;

public class AlarmService extends MonoService<AlarmSubscriber> {
    private static AlarmService instance;
    private Alarm currentAlarm;

    private AlarmService() {}

    public static AlarmService getInstance() {
        if (instance == null) {
            instance = new AlarmService();
        }

        return instance;
    }

    public Alarm get() {
        return (Alarm) currentAlarm.clone();
    }

    public void set(Alarm nextAlarm) {
        currentAlarm = nextAlarm;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(AlarmSubscriber subscriber) {
        subscriber.update(currentAlarm);
    }
}
