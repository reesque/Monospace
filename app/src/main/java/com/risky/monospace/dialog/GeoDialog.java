package com.risky.monospace.dialog;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.GeoPosition;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.WeatherService;

public class GeoDialog extends Dialog {
    private final Context context;
    private EditText latitude;
    private EditText longitude;
    private Button okBtn;
    private Button cancelBtn;

    public GeoDialog(@NonNull Context context) {
        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.geo_dialog);

        latitude = findViewById(R.id.lat_input);
        longitude = findViewById(R.id.long_input);
        okBtn = findViewById(R.id.geo_confirm);
        cancelBtn = findViewById(R.id.geo_cancel);

        cancelBtn.setOnClickListener(v -> dismiss());
        okBtn.setOnClickListener(v -> {
            try {
                double lat = Double.parseDouble(latitude.getText().toString());
                double lon = Double.parseDouble(longitude.getText().toString());

                SharedPreferences sharedPref =
                        context.getSharedPreferences("settings", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("weatherLat", String.valueOf(lat));
                editor.putString("weatherLong", String.valueOf(lon));
                editor.apply();

                WeatherService.getInstance().set(new GeoPosition(lat, lon));
            } catch (NumberFormatException e) {
                // Ignore, since it's not valid input
            }
            dismiss();
        });
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.GEO);

        super.dismiss();
    }
}
