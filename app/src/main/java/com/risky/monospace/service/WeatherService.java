package com.risky.monospace.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.risky.monospace.model.GeoPosition;
import com.risky.monospace.model.WeatherCondition;
import com.risky.monospace.service.subscribers.WeatherSubscriber;
import com.risky.monospace.util.NetworkUtil;
import com.risky.monospace.util.PermissionHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherService extends MonoService<WeatherSubscriber> {
    private static final int UPDATE_INTERVAL = 3600000;
    private static WeatherService instance;
    private Double temperature;
    private WeatherCondition condition;
    private Handler weatherHandler;
    private HandlerThread weatherThread;
    private Runnable weatherRunner;
    private LocationListener locationListener;

    private WeatherService(Context context) {
        weatherThread = new HandlerThread("WeatherThread");
        weatherThread.start();
        weatherHandler = new Handler(weatherThread.getLooper());
        weatherRunner = new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (!PermissionHelper.checkLocation(context)) {
                    return;
                }

                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

                weatherHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        weatherHandler.post(weatherRunner);
    }

    public static WeatherService getInstance(Context context) {
        if (instance == null) {
            instance = new WeatherService(context);
            instance.locationListener = location -> {
                GeoPosition position = new GeoPosition(location.getLatitude(), location.getLongitude());

                try {
                    JSONObject response = NetworkUtil.getJSONObjectFromURL(
                            "https://api.open-meteo.com/v1/forecast?&latitude="
                                    + position.latitude + "&longitude="
                                    + position.longitude + "&current=temperature_2m,weather_code");

                    instance.temperature = response.getJSONObject("current").getDouble("temperature_2m");
                    instance.condition = WeatherCondition.getCondition(
                            response.getJSONObject("current").getInt("weather_code"));
                } catch (IOException | JSONException e) {
                    instance.temperature = null;
                    instance.condition = null;
                }

                new Handler(Looper.getMainLooper()).post(
                        () -> WeatherService.getInstance(context).notifySubscriber());
            };
        }

        return instance;
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
        subscriber.update(temperature, condition);
    }
}
