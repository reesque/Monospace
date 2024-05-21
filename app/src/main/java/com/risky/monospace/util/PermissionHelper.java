package com.risky.monospace.util;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.risky.monospace.MainActivity;
import com.risky.monospace.receiver.NotificationReceiver;

public class PermissionHelper {
    public static final int FINE_LOCATION_REQUEST_CODE = 10;

    public static void requestFineLocation(Context context) {
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ((MainActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    public static boolean checkLocation(Context context) {
        return checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkNotification() {
        return NotificationReceiver.checkPermission();
    }

    public static boolean drawOverApps(Context context) {
        return Settings.canDrawOverlays(context);
    }
}
