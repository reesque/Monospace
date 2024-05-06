package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.risky.monospace.service.AppPackageService;

public class AppPackageReceiver extends BroadcastReceiver {
    public AppPackageReceiver(Context context) {
        AppPackageService.getInstance().refresh(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REMOVED:
                AppPackageService.getInstance().refresh(context);
                break;
        }
    }
}
