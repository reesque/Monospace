package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.CurrentWeatherState;
import com.risky.monospace.model.WeatherForecast;

public interface WeatherSubscriber extends MonoSubscriber {
    void update(CurrentWeatherState state);

    void update(WeatherForecast forecast);
}
