package com.risky.monospace.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.risky.monospace.R;
import com.risky.monospace.model.CurrentWeatherState;
import com.risky.monospace.model.WeatherForecast;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.WeatherSubscriber;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WeatherDialog extends MonoDialog implements WeatherSubscriber {
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
    private LineChart weatherChart;
    private static class HourFormatter extends ValueFormatter {
        @SuppressLint("DefaultLocale")
        @Override
        public String getFormattedValue(float value) {
            // Return the value as an integer string
            return String.format("%02d:00", Math.round(value));
        }
    }

    private static class YAxisFormatter extends ValueFormatter {

        @Override
        public String getPointLabel(Entry entry) {
            return entry.getX() % 4 == 0 ? String.valueOf(entry.getY()) : "";
        }
    }

    public WeatherDialog(@NonNull Context context, int themeResId, float dimAlpha) {
        super(context, themeResId, dimAlpha);
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

        // Avoid mem leak
        todayIcon = null;
        todayCondition = null;
        todayTemp = null;
        day1Icon = null;
        day1Dow = null;
        day1Temp = null;
        day2Icon = null;
        day2Dow = null;
        day2Temp = null;
        day3Icon = null;
        day3Dow = null;
        day3Temp = null;
        day4Icon = null;
        day4Dow = null;
        day4Temp = null;
        day5Icon = null;
        day5Dow = null;
        day5Temp = null;
        day6Icon = null;
        day6Dow = null;
        day6Temp = null;
        weatherChart = null;
    }

    @Override
    protected int layout() {
        return R.layout.weather_forecast_dialog;
    }

    @Override
    protected void initialize() {
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

        weatherChart = findViewById(R.id.weather_chart);
        weatherChart.getDescription().setEnabled(false);
        weatherChart.setTouchEnabled(false);
        weatherChart.setDragEnabled(false);
        weatherChart.setScaleEnabled(false);
        weatherChart.setPinchZoom(false);
        weatherChart.setDrawGridBackground(false);
        weatherChart.getLegend().setEnabled(false);
        weatherChart.animateXY(500, 0);
        weatherChart.setNoDataText(getContext().getString(R.string.widget_weather_no_data));
        weatherChart.setNoDataTextColor(getContext().getColor(R.color.black));

        YAxis y_left = weatherChart.getAxisLeft();
        y_left.setDrawGridLines(false);
        y_left.setDrawAxisLine(false);
        y_left.setDrawLabels(false);

        YAxis y_right = weatherChart.getAxisRight();
        y_right.setDrawGridLines(false);
        y_right.setDrawAxisLine(false);
        y_right.setDrawLabels(false);

        XAxis x = weatherChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setValueFormatter(new HourFormatter());
        x.setGridColor(getContext().getColor(R.color.black));
        x.setTextColor(getContext().getColor(R.color.black));
        x.setTextSize(11f);
        x.setDrawGridLines(false);

        WeatherService.getInstance(getContext()).subscribe(this);
    }

    @Override
    public void update(CurrentWeatherState state) {
        if (isDestroyed) {
            return;
        }

        if (state != null) {
            this.todayTemp.setText(state.temperature);
            this.todayCondition.setText(state.condition.getDisplayName());
            this.todayIcon.setImageResource(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 18
                    && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 5
                    ? state.condition.getIconDay() : state.condition.getIconNight());

            ArrayList<Entry> data = new ArrayList<>();
            List<Float> hourlyTemp = state.getHourlyTemp();
            for (int i = 0; i < hourlyTemp.size(); i++) {
                data.add(new Entry(i, hourlyTemp.get(i)));
            }

            LineDataSet dataSet;
            if (weatherChart.getData() != null && weatherChart.getData().getDataSetCount() > 0) {
                dataSet = (LineDataSet) weatherChart.getData().getDataSetByIndex(0);
                dataSet.setValues(data);
                weatherChart.getData().notifyDataChanged();
                weatherChart.notifyDataSetChanged();
            } else {
                dataSet = new LineDataSet(data, "");
                dataSet.setValueFormatter(new YAxisFormatter());
                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setCubicIntensity(0.2f);
                dataSet.setDrawFilled(true);
                dataSet.setDrawCircles(false);
                dataSet.setHighLightColor(getContext().getColor(R.color.black));
                dataSet.setColor(getContext().getColor(R.color.black));
                dataSet.setFillColor(getContext().getColor(R.color.black));
                dataSet.setFillAlpha(100);

                LineData lineData = new LineData(dataSet);
                lineData.setValueTextColor(getContext().getColor(R.color.black));
                lineData.setValueTextSize(11f);
                lineData.setDrawValues(true);

                weatherChart.setData(lineData);
            }

            weatherChart.invalidate();
            return;
        }

        this.todayTemp.setText(getContext().getString(R.string.widget_none_desc));
        this.todayCondition.setText(getContext().getString(R.string.widget_none_desc));
        this.todayIcon.setImageResource(R.drawable.no_connection_black);
    }

    @Override
    public void update(WeatherForecast forecast) {
        if (isDestroyed) {
            return;
        }

        if (forecast != null) {
            this.day1Temp.setText(forecast.getForecast(0).temperature);
            this.day1Dow.setText(forecast.getForecast(0).dayOfWeek);
            this.day1Icon.setImageResource(forecast.getForecast(0).condition.getIconDay());

            this.day2Temp.setText(forecast.getForecast(1).temperature);
            this.day2Dow.setText(forecast.getForecast(1).dayOfWeek);
            this.day2Icon.setImageResource(forecast.getForecast(1).condition.getIconDay());

            this.day3Temp.setText(forecast.getForecast(2).temperature);
            this.day3Dow.setText(forecast.getForecast(2).dayOfWeek);
            this.day3Icon.setImageResource(forecast.getForecast(2).condition.getIconDay());

            this.day4Temp.setText(forecast.getForecast(3).temperature);
            this.day4Dow.setText(forecast.getForecast(3).dayOfWeek);
            this.day4Icon.setImageResource(forecast.getForecast(3).condition.getIconDay());

            this.day5Temp.setText(forecast.getForecast(4).temperature);
            this.day5Dow.setText(forecast.getForecast(4).dayOfWeek);
            this.day5Icon.setImageResource(forecast.getForecast(4).condition.getIconDay());

            this.day6Temp.setText(forecast.getForecast(5).temperature);
            this.day6Dow.setText(forecast.getForecast(5).dayOfWeek);
            this.day6Icon.setImageResource(forecast.getForecast(5).condition.getIconDay());

            return;
        }

        this.day1Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day1Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day1Icon.setImageResource(R.drawable.no_connection_black);

        this.day2Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day2Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day2Icon.setImageResource(R.drawable.no_connection_black);

        this.day3Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day3Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day3Icon.setImageResource(R.drawable.no_connection_black);

        this.day4Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day4Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day4Icon.setImageResource(R.drawable.no_connection_black);

        this.day5Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day5Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day5Icon.setImageResource(R.drawable.no_connection_black);

        this.day6Temp.setText(getContext().getString(R.string.widget_none_desc));
        this.day6Dow.setText(getContext().getString(R.string.widget_none_desc));
        this.day6Icon.setImageResource(R.drawable.no_connection_black);
    }
}
