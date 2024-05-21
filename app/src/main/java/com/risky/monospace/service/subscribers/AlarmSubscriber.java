package com.risky.monospace.service.subscribers;

import java.util.Calendar;

public interface AlarmSubscriber extends MonoSubscriber {
    void update(Calendar nextAlarm);
}
