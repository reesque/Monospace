package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.Alarm;

public interface AlarmSubscriber extends MonoSubscriber {
    void update(Alarm nextAlarm);
}
