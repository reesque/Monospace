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

public class WeatherService extends MonoService<WeatherSubscriber> {
    private static final int UPDATE_INTERVAL = 3600000;
    private static WeatherService instance;
    private GeoPosition position = new GeoPosition(0.0, 0.0);
    private Double temperature;
    private WeatherCondition condition;
    private Handler weatherHandler;
    private HandlerThread weatherThread;
    private Runnable weatherRunner;

    private WeatherService() {
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

                new Handler(Looper.getMainLooper()).post(
                        () -> WeatherService.getInstance().notifySubscriber());
                weatherHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        };

        weatherHandler.post(weatherRunner);
    }

    public static WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }

        return instance;
    }

    public void set(GeoPosition pos) {
        position = pos;

        update();
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
