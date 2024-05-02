package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.risky.monospace.util.BatterySubscriber;

public class BatteryReceiver extends BroadcastReceiver {
    private BatterySubscriber subscriber;

    public BatteryReceiver(BatterySubscriber subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int raw = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int level = -1;
        if (raw >= 0 && scale > 0) {
            level = (raw * 100) / scale;
        }

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING;
        boolean isFull = status == BatteryManager.BATTERY_STATUS_FULL;

        subscriber.update(level, isCharging, isFull);
    }
}
