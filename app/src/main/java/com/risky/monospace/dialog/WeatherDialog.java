package com.risky.monospace.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.Media;
import com.risky.monospace.model.WeatherForecast;
import com.risky.monospace.model.WeatherState;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.MediaSubscriber;
import com.risky.monospace.service.subscribers.WeatherSubscriber;

import java.util.Calendar;

public class WeatherDialog extends Dialog implements WeatherSubscriber {
    private ImageView todayIcon;
    private TextView todayCondition;
    private TextView todayTemp;
    private ImageView day1Icon;
    private TextView day1Dow;
    private TextView day1Temp;
    private ImageView day2Icon;
    private TextView day2Dow;
    private TextView day2Temp;
    private ImageView day3Icon;
    private TextView day3Dow;
    private TextView day3Temp;
    private ImageView day4Icon;
    private TextView day4Dow;
    private TextView day4Temp;
    private ImageView day5Icon;
    private TextView day5Dow;
    private TextView day5Temp;
    private ImageView day6Icon;
    private TextView day6Dow;
    private TextView day6Temp;


    public WeatherDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.weather_forecast_dialog);

        todayIcon = findViewById(R.id.weather_today_icon);
        todayCondition = findViewById(R.id.weather_today_condition);
        todayTemp = findViewById(R.id.weather_today_temp);

        day1Icon = findViewById(R.id.weather_day1_icon);
        day1Dow = findViewById(R.id.weather_day1_dow);
        day1Temp = findViewById(R.id.weather_day1_temp);

        day2Icon = findViewById(R.id.weather_day2_icon);
        day2Dow = findViewById(R.id.weather_day2_dow);
        day2Temp = findViewById(R.id.weather_day2_temp);

        day3Icon = findViewById(R.id.weather_day3_icon);
        day3Dow = findViewById(R.id.weather_day3_dow);
        day3Temp = findViewById(R.id.weather_day3_temp);

        day4Icon = findViewById(R.id.weather_day4_icon);
        day4Dow = findViewById(R.id.weather_day4_dow);
        day4Temp = findViewById(R.id.weather_day4_temp);

        day5Icon = findViewById(R.id.weather_day5_icon);
        day5Dow = findViewById(R.id.weather_day5_dow);
        day5Temp = findViewById(R.id.weather_day5_temp);

        day6Icon = findViewById(R.id.weather_day6_icon);
        day6Dow = findViewById(R.id.weather_day6_dow);
        day6Temp = findViewById(R.id.weather_day6_temp);

        WeatherService.getInstance(getContext()).subscribe(this);
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.WEATHER);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        WeatherService.getInstance(getContext()).unsubscribe(this);
    }

    @Override
    public void update(WeatherState state) {
        // Don't need to handle
    }

    @Override
    public void update(WeatherForecast forecast) {
        if (forecast != null) {
            this.todayTemp.setText(forecast.getForecast(0).temperature);
            this.todayCondition.setText(forecast.getForecast(0).condition.getDisplayName());
            this.todayIcon.setImageResource(
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 18 && Calendar.getInstance().get(Calendar.HOUR_OF_DAY)  >= 5
                    ? forecast.getForecast(0).condition.getIconDay() : forecast.getForecast(0).condition.getIconNight());

            this.day1Temp.setText(forecast.getForecast(1).temperature);
            this.day1Dow.setText(forecast.getForecast(1).dayOfWeek);
            this.day1Icon.setImageResource(forecast.getForecast(1).condition.getIconDay());

            this.day2Temp.setText(forecast.getForecast(2).temperature);
            this.day2Dow.setText(forecast.getForecast(2).dayOfWeek);
            this.day2Icon.setImageResource(forecast.getForecast(2).condition.getIconDay());

            this.day3Temp.setText(forecast.getForecast(3).temperature);
            this.day3Dow.setText(forecast.getForecast(3).dayOfWeek);
            this.day3Icon.setImageResource(forecast.getForecast(3).condition.getIconDay());

            this.day4Temp.setText(forecast.getForecast(4).temperature);
            this.day4Dow.setText(forecast.getForecast(4).dayOfWeek);
            this.day4Icon.setImageResource(forecast.getForecast(4).condition.getIconDay());

            this.day5Temp.setText(forecast.getForecast(5).temperature);
            this.day5Dow.setText(forecast.getForecast(5).dayOfWeek);
            this.day5Icon.setImageResource(forecast.getForecast(5).condition.getIconDay());

            this.day6Temp.setText(forecast.getForecast(6).temperature);
            this.day6Dow.setText(forecast.getForecast(6).dayOfWeek);
            this.day6Icon.setImageResource(forecast.getForecast(6).condition.getIconDay());

            return;
        }

        this.todayTemp.setText("None");
        this.todayCondition.setText("None");
        this.todayIcon.setImageResource(R.drawable.no_service);

        this.day1Temp.setText("None");
        this.day1Dow.setText("None");
        this.day1Icon.setImageResource(R.drawable.no_service);

        this.day2Temp.setText("None");
        this.day2Dow.setText("None");
        this.day2Icon.setImageResource(R.drawable.no_service);

        this.day3Temp.setText("None");
        this.day3Dow.setText("None");
        this.day3Icon.setImageResource(R.drawable.no_service);

        this.day4Temp.setText("None");
        this.day4Dow.setText("None");
        this.day4Icon.setImageResource(R.drawable.no_service);

        this.day5Temp.setText("None");
        this.day5Dow.setText("None");
        this.day5Icon.setImageResource(R.drawable.no_service);

        this.day6Temp.setText("None");
        this.day6Dow.setText("None");
        this.day6Icon.setImageResource(R.drawable.no_service);
    }
}
