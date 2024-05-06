package com.risky.monospace.service;

import com.risky.monospace.model.LocationStatus;
import com.risky.monospace.service.subscribers.LocationSubscriber;

public class LocationService extends MonoService<LocationSubscriber> {
    private static LocationService instance;
    private LocationStatus locationStatus = LocationStatus.UNAVAILABLE;

    private LocationService() {}

    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }

        return instance;
    }

    public void set(LocationStatus status) {
        locationStatus = status;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(LocationSubscriber subscriber) {
        subscriber.update(locationStatus);
    }
}
