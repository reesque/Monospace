package com.risky.monospace.model;

import java.util.List;

public class WeatherForecast {
    private final List<UpcomingWeatherState> forecast;

    public WeatherForecast(List<UpcomingWeatherState> forecast) {
        this.forecast = forecast;
    }

    public UpcomingWeatherState getForecast(int day) {
        return forecast.get(day);
    }
}
