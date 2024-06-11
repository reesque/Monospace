package com.risky.monospace.util;

import static android.os.Looper.getMainLooper;
import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import com.risky.monospace.receiver.NotificationReceiver;

public class PermissionHelper {
    public static final int FINE_LOCATION_REQUEST_CODE = 10;
    public static final int ACCESS_AIRPOD_INFORMATION_REQUEST_CODE = 20;
    public static final int CALENDAR_REQUEST_CODE = 30;
    public static final String ACCESS_AIRPOD_INFORMATION = "com.dosse.airpods.permission.ACCESS_AIRPOD_INFORMATION";

    public static void requestFineLocation(Context context) {
        if (checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ((AppCompatActivity) context).requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST_CODE);
        }
    }

    public static void requestAirpod(Context context) {
        if (checkSelfPermission(context, ACCESS_AIRPOD_INFORMATION) == PackageManager.PERMISSION_DENIED) {
            ((AppCompatActivity) context).requestPermissions(new String[]{ACCESS_AIRPOD_INFORMATION}, ACCESS_AIRPOD_INFORMATION_REQUEST_CODE);
        }
    }

    public static void requestDrawOverApps(Context context) {
        if (!Settings.canDrawOverlays(context)) {
            // Has to run on main thread
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:com.risky.monospace"));
            new Handler(getMainLooper()).post(() -> context.startActivity(intent));
        }
    }

    public static void requestNotificationAccess(Context context) {
        if (!checkNotificationAccess()) {
            // Has to run on main thread
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            new Handler(getMainLooper()).post(() -> context.startActivity(intent));
        }
    }

    public static void requestCalendar(Context context) {
        if (checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED) {
            ((AppCompatActivity) context).requestPermissions(new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_REQUEST_CODE);
        }
    }

    public static boolean checkLocation(Context context) {
        return checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkNotificationAccess() {
        return NotificationReceiver.checkPermission();
    }

    public static boolean checkAirpod(Context context) {
        return checkSelfPermission(context, ACCESS_AIRPOD_INFORMATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean checkDrawOverApps(Context context) {
        return Settings.canDrawOverlays(context);
    }

    public static boolean checkCalendar(Context context) {
        return checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }
}
