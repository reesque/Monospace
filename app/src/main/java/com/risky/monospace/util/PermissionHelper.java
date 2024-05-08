package com.risky.monospace.util;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.risky.monospace.MainActivity;
import com.risky.monospace.receiver.NotificationReceiver;

public class PermissionHelper {
    public static final int FINE_LOCATION_REQUEST_CODE = 10;
    public static final int BACKGROUND_LOCATION_REQUEST_CODE = 20;

    public static void requestFineLocation(Context context) {
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ((MainActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    public static void requestBackgroundLocation(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
                ((MainActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_REQUEST_CODE);
            }
        }
    }

    public static boolean checkLocation(Context context) {
        boolean fineLocation = checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean backgroundLocation = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocation =  checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        return fineLocation && backgroundLocation;
    }

    public static boolean checkNotification() {
        return NotificationReceiver.checkPermission();
    }
}
