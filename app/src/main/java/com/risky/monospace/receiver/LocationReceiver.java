package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.risky.monospace.model.LocationStatus;
import com.risky.monospace.service.LocationService;

public class LocationReceiver extends BroadcastReceiver {
    public LocationReceiver(Context context) {
        update(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        update(context);
    }

    private void update(Context context) {
        final LocationManager manager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationService.getInstance().set(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ? LocationStatus.ON : LocationStatus.UNAVAILABLE);
    }
}
