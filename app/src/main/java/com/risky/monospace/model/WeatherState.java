package com.risky.monospace.model;

public class WeatherState {
    public final String dayOfWeek;
    public final String temperature;
    public final WeatherCondition condition;

    public WeatherState(String dayOfWeek, String temperature, WeatherCondition condition) {
        this.dayOfWeek = dayOfWeek;
        this.temperature = temperature;
        this.condition = condition;
    }
}
