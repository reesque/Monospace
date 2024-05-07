package com.risky.monospace.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;

import com.risky.monospace.R;
import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.model.Media;
import com.risky.monospace.model.Notification;
import com.risky.monospace.model.Pod;
import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;
import com.risky.monospace.model.WeatherCondition;
import com.risky.monospace.service.AirpodService;
import com.risky.monospace.service.BluetoothService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.AirpodSubcriber;
import com.risky.monospace.service.subscribers.MediaSubscriber;
import com.risky.monospace.service.subscribers.NotificationSubscriber;
import com.risky.monospace.service.subscribers.WeatherSubscriber;

public class GreetFragment extends Fragment
        implements NotificationSubscriber, WeatherSubscriber, MediaSubscriber, AirpodSubcriber {
    private View view;
    private Context context;
    private TextView temperature;
    private TextView track;
    private ImageView weatherIcon;
    private ImageView mediaIcon;
    private ImageView airpodIcon;
    private ImageView notifIcon;
    private LinearLayout notificationPanel;
    private LinearLayout airpodPanel;
    private LinearLayout mediaPanel;

    public GreetFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.greet_fragment, container, false);

        notificationPanel = view.findViewById(R.id.notification_container);
        temperature = view.findViewById(R.id.weather_temp);
        weatherIcon = view.findViewById(R.id.weather_icon);
        airpodIcon = view.findViewById(R.id.airpod_icon);
        mediaIcon = view.findViewById(R.id.media_icon);
        track = view.findViewById(R.id.media_track);
        notifIcon = view.findViewById(R.id.notification_icon);
        airpodPanel = view.findViewById(R.id.airpod_container);
        mediaPanel = view.findViewById(R.id.media_container);

        NotificationService.getInstance().subscribe(this);
        WeatherService.getInstance().subscribe(this);
        MediaService.getInstance().subscribe(this);
        AirpodService.getInstance().subscribe(this);

        weatherIcon.setOnLongClickListener(v -> {
            DialogService.getInstance().show(context, DialogType.GEO);
            return true;
        });

        airpodIcon.setOnClickListener(v -> DialogService.getInstance().show(context, DialogType.AIRPOD));

        weatherIcon.setOnClickListener(v -> WeatherService.getInstance().update());

        notifIcon.setOnLongClickListener(v -> {
            Intent intent =
                    new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
            return true;
        });

        // Funny scrolling title
        track.setSelected(true);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationService.getInstance().unsubscribe(this);
        WeatherService.getInstance().unsubscribe(this);
        MediaService.getInstance().unsubscribe(this);
        AirpodService.getInstance().unsubscribe(this);
    }

    @Override
    public void update(List<Notification> notifications) {
        // Clear child elements
        notificationPanel.removeAllViews();

        int notificationCount = notifications.size();
        for (Notification n : notifications) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);

            if (notificationCount == notifications.size() - 5) {
                TextView moreIcon = new TextView(context);
                moreIcon.setText("+" + notificationCount);
                params.setMargins(8, 0, 0, 0);
                moreIcon.setLayoutParams(params);
                moreIcon.setTextColor(ContextCompat.getColor(context, R.color.white));
                moreIcon.setTextSize(10);
                moreIcon.setGravity(Gravity.CENTER);
                moreIcon.setTypeface(moreIcon.getTypeface(), Typeface.BOLD);
                moreIcon.setBackgroundResource(R.drawable.round_black);

                notificationPanel.addView(moreIcon);
                break;
            }

            Drawable icon = null;
            try {
                Resources res = context.getPackageManager()
                        .getResourcesForApplication(n.packageName);
                icon = ResourcesCompat.getDrawable(res, n.icon, null);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            ImageView notifIcon = new ImageView(context);
            params.setMargins(0, 0, 10, 0);
            notifIcon.setLayoutParams(params);
            notifIcon.setImageDrawable(icon);
            notifIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));

            notificationPanel.addView(notifIcon);
            notificationCount--;
        }
    }

    @Override
    public void update(Double temperature, WeatherCondition condition) {
        if (condition != null) {
            this.temperature.setText(temperature + "°C");
            this.weatherIcon.setImageResource(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <= 18
                    ? condition.getIconDay() : condition.getIconNight());
        } else {
            this.temperature.setText("None");
            this.weatherIcon.setImageResource(R.drawable.no_service);
        }
    }

    @Override
    public void update(Media media) {
        if (media == null) {
            mediaPanel.setVisibility(View.GONE);
            track.setText("None");
            mediaIcon.setOnClickListener(null);
            return;
        }

        mediaPanel.setVisibility(View.VISIBLE);

        if (media.packageName != null) {
            mediaIcon.setOnClickListener(v -> {
                Intent launchIntent =
                        context.getPackageManager().getLaunchIntentForPackage(media.packageName);
                startActivity(launchIntent);
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
}
