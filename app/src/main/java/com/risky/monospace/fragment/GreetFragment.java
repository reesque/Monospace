package com.risky.monospace.fragment;

import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.risky.monospace.R;
import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.model.Alarm;
import com.risky.monospace.model.CurrentWeatherState;
import com.risky.monospace.model.Media;
import com.risky.monospace.model.Pod;
import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;
import com.risky.monospace.model.WeatherForecast;
import com.risky.monospace.service.AirpodService;
import com.risky.monospace.service.AlarmService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.AirpodSubscriber;
import com.risky.monospace.service.subscribers.AlarmSubscriber;
import com.risky.monospace.service.subscribers.MediaSubscriber;
import com.risky.monospace.service.subscribers.WeatherSubscriber;
import com.risky.monospace.util.DTFormattertUtil;
import com.risky.monospace.util.PermissionHelper;

import java.util.Calendar;

public class GreetFragment extends Fragment
        implements WeatherSubscriber, MediaSubscriber, AirpodSubscriber, AlarmSubscriber {
    private TextView temperature;
    private TextView track;
    private ImageView weatherIcon;
    private ImageView mediaIcon;
    private ImageView airpodIcon;
    private TextView alarmEta;
    private ImageView alarmIcon;
    private LinearLayout airpodPanel;
    private LinearLayout mediaPanel;
    private LinearLayout alarmPanel;

    public GreetFragment() {
        // Empty
    }

    public static GreetFragment newInstance() {
        return new GreetFragment();
    }

    @SuppressLint("MissingPermission")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.greet_fragment, container, false);

        temperature = view.findViewById(R.id.weather_temp);
        weatherIcon = view.findViewById(R.id.weather_icon);
        airpodIcon = view.findViewById(R.id.airpod_icon);
        mediaIcon = view.findViewById(R.id.media_icon);
        track = view.findViewById(R.id.media_track);
        alarmEta = view.findViewById(R.id.alarm_time);
        alarmIcon = view.findViewById(R.id.alarm_icon);
        airpodPanel = view.findViewById(R.id.airpod_container);
        mediaPanel = view.findViewById(R.id.media_container);
        alarmPanel = view.findViewById(R.id.alarm_container);

        WeatherService.getInstance(getContext()).subscribe(this);
        MediaService.getInstance().subscribe(this);
        AirpodService.getInstance().subscribe(this);
        AlarmService.getInstance().subscribe(this);

        weatherIcon.setOnClickListener(v -> DialogService.getInstance().show(getContext(), DialogType.WEATHER, null));

        weatherIcon.setOnLongClickListener(v -> {
            if (!PermissionHelper.checkLocation(getContext())) {
                // Has to run on main thread
                new Handler(getMainLooper()).post(() -> PermissionHelper.requestFineLocation(getContext()));
                return true;
            }

            temperature.setText(getActivity().getString(R.string.widget_weather_updating));
            WeatherService.getInstance(getContext()).locationUpdate();

            return true;
        });

        airpodIcon.setOnClickListener(v -> DialogService.getInstance().show(getContext(), DialogType.AIRPOD, null));

        // Funny scrolling title
        track.setSelected(true);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WeatherService.getInstance(getContext()).unsubscribe(this);
        MediaService.getInstance().unsubscribe(this);
        AirpodService.getInstance().unsubscribe(this);
        AlarmService.getInstance().unsubscribe(this);

        // Avoid mem leak
        temperature = null;
        track = null;
        weatherIcon = null;
        mediaIcon = null;
        airpodIcon = null;
        alarmEta = null;
        alarmIcon = null;
        airpodPanel = null;
        mediaPanel = null;
        alarmPanel = null;
    }

    @Override
    public void update(CurrentWeatherState state) {
        if (state != null) {
            this.temperature.setText(state.temperature);
            this.weatherIcon.setImageResource(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 18
                    && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 5
                    ? state.condition.getIconDay() : state.condition.getIconNight());
            return;
        }

        this.temperature.setText(getActivity().getString(R.string.widget_none_desc));
        this.weatherIcon.setImageResource(R.drawable.no_connection_black);
    }

    @Override
    public void update(WeatherForecast forecast) {
        // We don't need to handle
    }

    @Override
    public void update(Media media) {
        if (media == null) {
            track.setText(getActivity().getString(R.string.widget_none_desc));
            mediaPanel.setVisibility(View.GONE);
            mediaIcon.setOnClickListener(null);
            DialogService.getInstance().dismiss(DialogType.MEDIA);
            return;
        }

        mediaPanel.setVisibility(View.VISIBLE);
        mediaIcon.setOnClickListener(v -> DialogService.getInstance().show(getContext(), DialogType.MEDIA, null));

        if (media.packageName != null) {
            mediaIcon.setOnLongClickListener(v -> {
                Intent launchIntent = getContext().getPackageManager()
                        .getLaunchIntentForPackage(media.packageName);
                new Handler(getMainLooper()).post(() -> startActivity(launchIntent));

                return true;
            });
        }

        if (media.artist == null) {
            track.setText(media.track);
            return;
        }

        track.setText(String.format("%s - %s", media.track, media.artist));
    }

    @Override
    public void update(SinglePod pod) {
        updateAirpodUI(pod);
    }

    @Override
    public void update(RegularPod pod) {
        updateAirpodUI(pod);
    }

    private void updateAirpodUI(Pod pod) {
        if (pod == null || pod.isDisconnected) {
            airpodPanel.setVisibility(View.GONE);
        } else {
            airpodPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void update(Alarm nextAlarm) {
        if (nextAlarm == null) {
            alarmEta.setText(getActivity().getString(R.string.widget_none_desc));
            alarmPanel.setVisibility(View.GONE);
            alarmIcon.setOnClickListener(null);
            return;
        }

        alarmIcon.setOnClickListener(v -> {
            Intent launchIntent = getContext().getPackageManager()
                    .getLaunchIntentForPackage(nextAlarm.packageName);
            new Handler(getMainLooper()).post(() -> startActivity(launchIntent));
        });

        alarmEta.setText(DTFormattertUtil.alarmDisplay.format(nextAlarm.triggerTime.getTime()));
        alarmPanel.setVisibility(View.VISIBLE);
    }
}
