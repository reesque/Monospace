package com.risky.monospace.model;

public class UpcomingWeatherState extends WeatherState {
    public final String dayOfWeek;

    public UpcomingWeatherState(String temperature, WeatherCondition condition, String dayOfWeek) {
        super(temperature, condition);

        this.dayOfWeek = dayOfWeek;
    }
}
