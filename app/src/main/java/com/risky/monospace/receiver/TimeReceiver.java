package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeReceiver extends BroadcastReceiver {
    private final Runnable runnable;

    public TimeReceiver(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        runnable.run();
    }
}
