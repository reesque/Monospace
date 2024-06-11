package com.risky.monospace.service;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.risky.monospace.R;
import com.risky.monospace.model.CurrentWeatherState;
import com.risky.monospace.model.GeoPosition;
import com.risky.monospace.model.UpcomingWeatherState;
import com.risky.monospace.model.WeatherCondition;
import com.risky.monospace.model.WeatherForecast;
import com.risky.monospace.service.subscribers.WeatherSubscriber;
import com.risky.monospace.util.NetworkUtil;
import com.risky.monospace.util.PermissionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WeatherService extends MonoService<WeatherSubscriber> {
    private static final int UPDATE_INTERVAL = 3600000;
    private static WeatherService instance;
    private final Handler weatherHandler;
    private final HandlerThread weatherThread;
    private final Runnable weatherRunner;
    private final Runnable locationRunner;
    private CurrentWeatherState currentWeather;
    private WeatherForecast weatherForecast;
    private GeoPosition position;
    private boolean isLocationUpdating = false;

    @SuppressLint("MissingPermission")
    private WeatherService(Context context) {
        weatherThread = new HandlerThread("WeatherThread");
        weatherThread.start();
        weatherHandler = new Handler(weatherThread.getLooper());
        weatherRunner = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (!PermissionHelper.checkLocation(context) || position == null) {
                    return;
                }

                try {
                    Calendar currentTime = Calendar.getInstance();
                    TimeZone timeZone = currentTime.getTimeZone();
                    String timeZoneString = timeZone.getDisplayName(
                            timeZone.inDaylightTime(currentTime.getTime()), TimeZone.SHORT);
                    String unit = "celsius";
                    String unitAbbr = context.getString(R.string.settings_temp_unit_c);

                    if (!context.getSharedPreferences("settings", MODE_PRIVATE).getBoolean("useMetric", true)) {
                        unit = "fahrenheit";
                        unitAbbr = context.getString(R.string.settings_temp_unit_f);
                    }

                    JSONObject response = NetworkUtil.getJSONObjectFromURL(
                            "https://api.open-meteo.com/v1/forecast?&latitude="
                                    + position.latitude + "&longitude="
                                    + position.longitude + "&hourly=temperature_2m"
                                    + "&current=temperature_2m,weather_code"
                                    + "&forecast_days=7&daily=temperature_2m_min,"
                                    + "temperature_2m_max,weather_code&time_zone="
                                    + timeZoneString + "&temperature_unit=" + unit);

                    List<Float> hourlyTemp = new ArrayList<>();
                    JSONArray hourlyTempArray = response.getJSONObject("hourly").getJSONArray("temperature_2m");
                    for (int i = 0; i < 25; i++) {
                        hourlyTemp.add((float) hourlyTempArray.getDouble(i));
                    }

                    instance.currentWeather = new CurrentWeatherState(
                            (int) Math.round(response.getJSONObject("current").getDouble("temperature_2m")) + unitAbbr,
                            WeatherCondition.getCondition(response.getJSONObject("current").getInt("weather_code")), hourlyTemp);

                    List<UpcomingWeatherState> forecasts = new ArrayList<>();
                    JSONArray minArray = response.getJSONObject("daily").getJSONArray("temperature_2m_min");
                    JSONArray maxArray = response.getJSONObject("daily").getJSONArray("temperature_2m_max");
                    JSONArray codeArray = response.getJSONObject("daily").getJSONArray("weather_code");
                    JSONArray dateArray = response.getJSONObject("daily").getJSONArray("time");

                    for (int i = 1; i < minArray.length(); i++) {
                        forecasts.add(new UpcomingWeatherState(
                                (int) Math.round((minArray.getDouble(i) + maxArray.getDouble(i)) / 2) + unitAbbr,
                                WeatherCondition.getCondition(codeArray.getInt(i)), LocalDate.parse(dateArray.getString(i))
                                .getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault())));
                    }

                    instance.weatherForecast = new WeatherForecast(forecasts);
                } catch (IOException | JSONException e) {
                    instance.currentWeather = null;
                    instance.weatherForecast = null;
                }

                new Handler(Looper.getMainLooper()).post(
                        () -> WeatherService.getInstance(context).notifySubscriber());

                weatherHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        weatherHandler.post(weatherRunner);

        locationRunner = () -> {
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, location -> {
                WeatherService.getInstance(context).set(new GeoPosition(location.getLatitude(), location.getLongitude()));

                context.getSharedPreferences("settings", MODE_PRIVATE)
                        .edit().putString("currentLat", String.valueOf(location.getLatitude()))
                        .putString("currentLong", String.valueOf(location.getLongitude())).apply();
            });
            isLocationUpdating = false;
        };
    }

    public static WeatherService getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherService(context);

            SharedPreferences sharedPref = context.getSharedPreferences("settings", MODE_PRIVATE);

            String sLat = sharedPref.getString("currentLat", null);
            String sLong = sharedPref.getString("currentLong", null);

            if (sLat != null && sLong != null) {
                instance.set(new GeoPosition(Double.parseDouble(sLat), Double.parseDouble(sLong)));
            }
        }

        return instance;
    }

    public void set(GeoPosition position) {
        this.position = position;
        update();
    }

    public void locationUpdate() {
        if (isLocationUpdating) {
            return;
        }

        isLocationUpdating = true;
        locationRunner.run();
    }

    public void update() {
        weatherHandler.removeCallbacks(weatherRunner);
        weatherHandler.post(weatherRunner);
    }

    @Override
    public void unsubscribe(WeatherSubscriber subscriber) {
        super.unsubscribe(subscriber);

        if (noSubscriber()) {
            weatherHandler.removeCallbacks(weatherRunner);
            weatherThread.quit();
        }
    }

    @Override
    protected void updateSubscriber(WeatherSubscriber subscriber) {
        subscriber.update(currentWeather);
        subscriber.update(weatherForecast);
    }
}
