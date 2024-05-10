package com.risky.monospace.model;

import com.risky.monospace.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum WeatherCondition {
    CLEAR("Clear", R.drawable.clear_day, R.drawable.clear_night,0, 1),
    OVERCAST("Overcast", R.drawable.partly_cloudy_day, R.drawable.partly_cloudy_night, 2, 3),
    FOGGY("Foggy", R.drawable.foggy, R.drawable.foggy, 45, 48),
    DRIZZLE("Drizzle", R.drawable.drizzle, R.drawable.drizzle, 51, 53, 55),
    FREEZE_DRIZZLE("Freezing Drizzle", R.drawable.freezing_drizzle, R.drawable.freezing_drizzle, 56, 57),
    RAIN("Rain", R.drawable.rain, R.drawable.rain, 61, 80),
    HEAVY_RAIN("Heavy Rain", R.drawable.rain_heavy, R.drawable.rain_heavy, 63, 81, 65, 82),
    FREEZE_RAIN("Freezing Rain", R.drawable.rain_snow, R.drawable.rain_snow, 66, 67),
    LIGHT_SNOW("Light Snow", R.drawable.light_snow, R.drawable.light_snow, 71, 77, 85),
    SNOW("Snow", R.drawable.snow, R.drawable.snow, 73, 75, 86),
    THUNDERSTORM("Thunderstorm", R.drawable.thunderstorm, R.drawable.thunderstorm, 95, 96, 99);

    private final String displayName;
    private final int iconDay;
    private final int iconNight;
    private List<Integer> wmoCode;

    WeatherCondition(String displayName, int iconDay, int iconNight, int ... wmo) {
        this.displayName = displayName;
        this.iconDay = iconDay;
        this.iconNight = iconNight;
        this.wmoCode = Arrays.stream(wmo) .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
        this.wmoCode = Collections.unmodifiableList(this.wmoCode);
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getIconDay() {
        return iconDay;
    }

    public int getIconNight() {
        return iconNight;
    }

    public static WeatherCondition getCondition(int wmo) {
        for (WeatherCondition condition : values()) {
            if (condition.wmoCode.contains(wmo)) {
                return condition;
            }
        }

        return null;
    }
}
