package com.risky.monospace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.risky.monospace.service.WeatherService;
import com.risky.monospace.util.PermissionHelper;

import java.util.Locale;

public class SettingsActivity  extends AppCompatActivity {
    private TextView fineLocationRequestButton;
    private TextView notificationRequestButton;
    private TextView drawOverAppsRequestButton;
    private TextView airpodRequestButton;
    private TextView calendarRequestButton;
    private SwitchCompat tempUnit;
    private TextView weatherLocation;
    private TextView weatherLocateButton;
    private Spinner searchProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_settings);

        fineLocationRequestButton = findViewById(R.id.settings_fine_location);
        notificationRequestButton = findViewById(R.id.settings_notification);
        drawOverAppsRequestButton = findViewById(R.id.settings_draw_over_apps);
        airpodRequestButton = findViewById(R.id.settings_airpod);
        calendarRequestButton = findViewById(R.id.settings_calendar);
        tempUnit = findViewById(R.id.settings_weather_unit);
        weatherLocation = findViewById(R.id.settings_weather_location);
        weatherLocateButton = findViewById(R.id.settings_weather_locate);
        searchProvider = findViewById(R.id.settings_search_provider);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.search_provider,
                R.layout.spinner_item
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        searchProvider.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Permissions
        enablePermissionButton(fineLocationRequestButton, !PermissionHelper.checkLocation(this),
                () -> PermissionHelper.requestFineLocation(this));
        enablePermissionButton(notificationRequestButton, !PermissionHelper.checkNotificationAccess(),
                () -> PermissionHelper.requestNotificationAccess(this));
        enablePermissionButton(drawOverAppsRequestButton, !PermissionHelper.checkDrawOverApps(this),
                () -> PermissionHelper.requestDrawOverApps(this));
        enablePermissionButton(airpodRequestButton, !PermissionHelper.checkAirpod(this),
                () -> PermissionHelper.requestAirpod(this));
        enablePermissionButton(calendarRequestButton, !PermissionHelper.checkCalendar(this),
                () -> PermissionHelper.requestCalendar(this));

        // Weather
        SharedPreferences sharedPref = getSharedPreferences("settings", MODE_PRIVATE);

        String sLat = sharedPref.getString("currentLat", null);
        String sLong = sharedPref.getString("currentLong", null);

        if (sLat != null && sLong != null) {
            weatherLocation.setText(String.format("%s, %s", sLat, sLong));
            weatherLocateButton.setBackgroundResource(R.drawable.capsule_bg_white);
            weatherLocateButton.setOnClickListener(v -> {
                String uri = String.format(Locale.ENGLISH, "geo:%s,%s", sLat, sLong);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                new Handler(getMainLooper()).post(() -> startActivity(intent));
            });
        } else {
            weatherLocation.setText(getString(R.string.widget_none_desc));
            weatherLocateButton.setBackgroundResource(R.drawable.capsule_bg_gray);
            weatherLocateButton.setOnClickListener(null);
        }

        tempUnit.setOnCheckedChangeListener(null);
        tempUnit.setChecked(!sharedPref.getBoolean("useMetric", true));
        tempUnit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit().putBoolean("useMetric", !isChecked).apply();

            if (PermissionHelper.checkLocation(this)) {
                WeatherService.getInstance(this).locationUpdate();
            }
        });

        searchProvider.setOnItemSelectedListener(null);
        searchProvider.setSelection(sharedPref.getInt("searchProvider", 0));
        searchProvider.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSharedPreferences("settings", MODE_PRIVATE)
                        .edit().putInt("searchProvider", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void enablePermissionButton(TextView textView, boolean isEnabled, Runnable onClickIfEnabled) {
        if (isEnabled) {
            textView.setText(getString(R.string.settings_permission_allow));
            textView.setBackgroundResource(R.drawable.capsule_bg_white);
            textView.setOnClickListener(v -> onClickIfEnabled.run());
        } else {
            textView.setText(getString(R.string.settings_permission_granted));
            textView.setBackgroundResource(R.drawable.capsule_bg_gray);
            textView.setOnClickListener(null);
        }
    }
}
