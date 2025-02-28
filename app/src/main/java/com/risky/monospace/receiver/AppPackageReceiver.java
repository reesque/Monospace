package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.risky.monospace.service.AppPackageService;

public class AppPackageReceiver extends BroadcastReceiver {
    public AppPackageReceiver(Context context) {
        AppPackageService.getInstance(context).refresh(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {
            AppPackageService.getInstance(context).refresh(context);
        }
    }
}
