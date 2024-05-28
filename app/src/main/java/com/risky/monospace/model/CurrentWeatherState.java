package com.risky.monospace.model;

import android.util.Log;

import java.util.Collections;
import java.util.List;

public class CurrentWeatherState extends WeatherState{
    private final List<Float> hourlyTemp;
    public CurrentWeatherState(String temperature, WeatherCondition condition, List<Float> hourlyTemp) {
        super(temperature, condition);

        this.hourlyTemp = hourlyTemp;
    }

    public List<Float> getHourlyTemp() {
        return Collections.unmodifiableList(hourlyTemp);
    }
}
