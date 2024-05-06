package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.WeatherCondition;

public interface WeatherSubscriber extends MonoSubscriber {
    void update(Double temperature, WeatherCondition condition);
}
