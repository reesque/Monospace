package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.WeatherForecast;
import com.risky.monospace.model.WeatherState;

public interface WeatherSubscriber extends MonoSubscriber {
    void update(WeatherState state);
    void update(WeatherForecast forecast);
}
