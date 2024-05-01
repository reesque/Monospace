package com.risky.simplify.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.risky.simplify.service.AppCacheService;

public class InstallReceiver extends BroadcastReceiver {
    public InstallReceiver(Context context) {
        AppCacheService.refresh(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case Intent.ACTION_PACKAGE_ADDED:
            case Intent.ACTION_PACKAGE_REMOVED:
                AppCacheService.refresh(context);
                break;
        }
    }
}
