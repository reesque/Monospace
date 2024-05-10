package com.risky.monospace.model;

import java.util.List;

public class WeatherForecast {
    private final List<WeatherState> forecast;

    public WeatherForecast(List<WeatherState> forecast) {
        this.forecast = forecast;
    }

    public WeatherState getForecast(int day) {
        return forecast.get(day);
    }
}
