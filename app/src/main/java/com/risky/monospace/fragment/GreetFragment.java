package com.risky.monospace.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.risky.monospace.dialog.GeoDialog;
import com.risky.monospace.model.Media;
import com.risky.monospace.model.Notification;
import com.risky.monospace.model.WeatherCondition;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.MediaSubscriber;
import com.risky.monospace.service.subscribers.NotificationSubscriber;
import com.risky.monospace.service.subscribers.WeatherSubscriber;

public class GreetFragment extends Fragment
        implements NotificationSubscriber, WeatherSubscriber, MediaSubscriber {
    private View view;
    private Context context;
    private TextView temperature;
    private TextView track;
    private ImageView weatherIcon;
    private ImageView notifIcon;
    private LinearLayout notificationPanel;

    // TEMPORARY
    private float LAT = 52.52f;
    private float LONG = 13.41f;

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
        track = view.findViewById(R.id.media_track);
        notifIcon = view.findViewById(R.id.notification_icon);

        NotificationService.subscribe(this);
        WeatherService.subscribe(this);
        MediaService.subscribe(this);

        weatherIcon.setOnLongClickListener(v -> {
            new GeoDialog(context).show();
            return true;
        });

        weatherIcon.setOnClickListener(v -> {
            WeatherService.update();
        });

        notifIcon.setOnClickListener(v -> {
            NotificationManager notificationManager = (
                    NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            NotificationService.clear();
        });

        notifIcon.setOnLongClickListener(v -> {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
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
        NotificationService.subscribe(null);
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
            track.setText("None");
            return;
        }

        if (media.artist == null) {
            track.setText(media.track);
            return;
        }

        track.setText(String.format("%s - %s", media.track, media.artist));
    }
}
