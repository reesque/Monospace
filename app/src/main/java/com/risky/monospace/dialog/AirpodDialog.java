package com.risky.monospace.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.risky.monospace.R;
import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;
import com.risky.monospace.service.AirpodService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.subscribers.AirpodSubscriber;

public class AirpodDialog extends MonoDialog implements AirpodSubscriber {
    private TextView model;
    private ImageView leftBattery;
    private ImageView leftBatteryCharge;
    private TextView leftPercentage;
    private ImageView centerBattery;
    private ImageView centerBatteryCharge;
    private TextView centerPercentage;
    private ImageView rightBattery;
    private ImageView rightBatteryCharge;
    private TextView rightPercentage;
    private ImageView leftIcon;
    private ImageView centerIcon;
    private ImageView rightIcon;
    private LinearLayout leftPanel;
    private LinearLayout rightPanel;

    public AirpodDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.AIRPOD);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        AirpodService.getInstance().unsubscribe(this);

        // Avoid mem leak
        model = null;
        leftBattery = null;
        centerBattery = null;
        rightBattery = null;
        leftBatteryCharge = null;
        centerBatteryCharge = null;
        rightBatteryCharge = null;
        leftPercentage = null;
        centerPercentage = null;
        rightPercentage = null;
        leftPanel = null;
        rightPanel = null;
        leftIcon = null;
        centerIcon = null;
        rightIcon = null;
    }

    @Override
    protected int layout() {
        return R.layout.airpod_dialog;
    }

    @Override
    protected void initialize() {
        model = findViewById(R.id.pod_model);
        leftBattery = findViewById(R.id.left_pod_battery);
        centerBattery = findViewById(R.id.center_pod_battery);
        rightBattery = findViewById(R.id.right_pod_battery);
        leftBatteryCharge = findViewById(R.id.left_pod_battery_charge);
        centerBatteryCharge = findViewById(R.id.center_pod_battery_charge);
        rightBatteryCharge = findViewById(R.id.right_pod_battery_charge);
        leftPercentage = findViewById(R.id.left_pod_percentage);
        centerPercentage = findViewById(R.id.center_pod_percentage);
        rightPercentage = findViewById(R.id.right_pod_percentage);
        leftPanel = findViewById(R.id.left_pod_container);
        rightPanel = findViewById(R.id.right_pod_container);
        leftIcon = findViewById(R.id.left_pod_icon);
        centerIcon = findViewById(R.id.center_pod_icon);
        rightIcon = findViewById(R.id.right_pod_icon);

        AirpodService.getInstance().subscribe(this);
    }

    @Override
    public void update(SinglePod pod) {
        if (isDestroyed) {
            return;
        }

        if (pod != null) {
            model.setText(pod.model.displayName);

            leftPanel.setVisibility(View.GONE);
            rightPanel.setVisibility(View.GONE);

            centerIcon.setImageResource(R.drawable.beats);

            centerIcon.setColorFilter(pod.status.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));

            centerBattery.setImageResource(getBatteryDrawable(pod.status));

            centerBattery.setColorFilter(pod.status.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));

            centerBatteryCharge.setVisibility(pod.isCharging ? View.VISIBLE : View.GONE);

            centerPercentage.setText(pod.status.isEmpty() ? "Unknown" : pod.status);

            centerPercentage.setTextColor(pod.status.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray) :
                    ContextCompat.getColor(getContext(), R.color.black));

            return;
        }

        dismiss();
    }

    @Override
    public void update(RegularPod pod) {
        if (isDestroyed) {
            return;
        }

        if (pod != null) {
            model.setText(pod.model.displayName);

            leftPanel.setVisibility(View.VISIBLE);
            rightPanel.setVisibility(View.VISIBLE);

            centerIcon.setImageResource(R.drawable.pod_case);

            leftIcon.setColorFilter(pod.leftStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));
            centerIcon.setColorFilter(pod.caseStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));
            rightIcon.setColorFilter(pod.rightStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));

            leftBattery.setImageResource(getBatteryDrawable(pod.leftStatus));
            centerBattery.setImageResource(getBatteryDrawable(pod.caseStatus));
            rightBattery.setImageResource(getBatteryDrawable(pod.rightStatus));

            leftBattery.setColorFilter(pod.leftStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));
            centerBattery.setColorFilter(pod.caseStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));
            rightBattery.setColorFilter(pod.rightStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray)
                    : ContextCompat.getColor(getContext(), R.color.black));

            leftBatteryCharge.setVisibility(pod.leftCharging ? View.VISIBLE : View.GONE);
            centerBatteryCharge.setVisibility(pod.caseCharging ? View.VISIBLE : View.GONE);
            rightBatteryCharge.setVisibility(pod.rightCharging ? View.VISIBLE : View.GONE);

            leftPercentage.setText(pod.leftStatus.isEmpty() ? "Unknown" : pod.leftStatus);
            centerPercentage.setText(pod.caseStatus.isEmpty() ? "Unknown" : pod.caseStatus);
            rightPercentage.setText(pod.rightStatus.isEmpty() ? "Unknown" : pod.rightStatus);

            leftPercentage.setTextColor(pod.leftStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray) :
                    ContextCompat.getColor(getContext(), R.color.black));
            centerPercentage.setTextColor(pod.caseStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray) :
                    ContextCompat.getColor(getContext(), R.color.black));
            rightPercentage.setTextColor(pod.rightStatus.isEmpty()
                    ? ContextCompat.getColor(getContext(), R.color.gray) :
                    ContextCompat.getColor(getContext(), R.color.black));

            return;
        }

        dismiss();
    }

    private int getBatteryDrawable(String percentage) {
        if (percentage.isEmpty()) {
            return R.drawable.battery0;
        }

        int intPercent = Integer.parseInt(String.valueOf(percentage.split("%")[0]));

        if (intPercent >= 75) {
            return R.drawable.battery4;
        } else if (intPercent >= 50) {
            return R.drawable.battery3;
        } else if (intPercent >= 25) {
            return R.drawable.battery2;
        } else {
            return R.drawable.battery1;
        }
    }
}
