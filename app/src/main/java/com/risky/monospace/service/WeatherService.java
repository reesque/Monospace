package com.risky.monospace.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.risky.monospace.model.GeoPosition;
import com.risky.monospace.model.WeatherCondition;
import com.risky.monospace.service.subscribers.WeatherSubscriber;
import com.risky.monospace.util.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherService {
    private static GeoPosition position = new GeoPosition(0.0, 0.0);
    private static Double temperature;
    private static WeatherCondition condition;
    private static WeatherSubscriber subscriber;
    private static Handler weatherHandler;
    private static HandlerThread weatherThread;
    private static Runnable weatherRunner;

    public static void set(GeoPosition pos) {
        position = pos;

        if (weatherHandler != null) {
            weatherHandler.removeCallbacks(weatherRunner);
            weatherHandler.post(weatherRunner);
        }
    }

    public static void initialize() {
        if (weatherHandler != null) {
            return;
        }

        weatherThread = new HandlerThread("WeatherThread");
        weatherThread.start();
        weatherHandler = new Handler(weatherThread.getLooper());
        weatherRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject response = NetworkUtil.getJSONObjectFromURL(
                            "https://api.open-meteo.com/v1/forecast?&latitude="
                                    + position.latitude + "&longitude="
                                    + position.longitude + "&current=temperature_2m,weather_code");

                    temperature = response.getJSONObject("current").getDouble("temperature_2m");
                    condition = WeatherCondition.getCondition(
                            response.getJSONObject("current").getInt("weather_code"));
                } catch (IOException | JSONException e) {
                    temperature = null;
                    condition = null;
                }

                new Handler(Looper.getMainLooper()).post(WeatherService::notifySubscriber);
                weatherHandler.postDelayed(this, 14400000);
            }
        };

        weatherHandler.post(weatherRunner);
    }

    public static void destroy() {
        weatherHandler.removeCallbacks(weatherRunner);
        weatherThread.quit();
    }

    public static void subscribe(WeatherSubscriber sub) {
        subscriber = sub;
        notifySubscriber();
    }

    public static void notifySubscriber() {
        if (subscriber != null) {
            subscriber.update(temperature, condition);
        }
    }
}
