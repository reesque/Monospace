package com.risky.monospace.model;

public abstract class WeatherState {
    public final String temperature;
    public final WeatherCondition condition;

    public WeatherState(String temperature, WeatherCondition condition) {
        this.temperature = temperature;
        this.condition = condition;
    }
}
