package com.risky.monospace.service;

import com.risky.monospace.model.BluetoothStatus;
import com.risky.monospace.model.ConnectivityType;
import com.risky.monospace.model.LocationStatus;
import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.util.BluetoothSubscriber;
import com.risky.monospace.util.LocationSubscriber;
import com.risky.monospace.util.NetworkSubscriber;

public class ConnectivityService {
    private static NetworkStatus networkStatus = NetworkStatus.UNAVAILABLE;
    private static BluetoothStatus bluetoothStatus = BluetoothStatus.UNAVAILABLE;
    private static LocationStatus locationStatus = LocationStatus.UNAVAILABLE;
    private static NetworkSubscriber networkSubscriber;
    private static BluetoothSubscriber bluetoothSubscriber;
    private static LocationSubscriber locationSubscriber;

    public static void set(NetworkStatus status) {
        networkStatus = status;
        notifySubscriber(ConnectivityType.NETWORK);
    }

    public static void set(BluetoothStatus status) {
        bluetoothStatus = status;
        notifySubscriber(ConnectivityType.BLUETOOTH);
    }

    public static void set(LocationStatus status) {
        locationStatus = status;
        notifySubscriber(ConnectivityType.LOCATION);
    }

    public static void subscribe(NetworkSubscriber networkSub) {
        networkSubscriber = networkSub;
        notifySubscriber(ConnectivityType.NETWORK);
    }

    public static void subscribe(BluetoothSubscriber bluetoothSub) {
        bluetoothSubscriber = bluetoothSub;
        notifySubscriber(ConnectivityType.BLUETOOTH);
    }

    public static void subscribe(LocationSubscriber locationSub) {
        locationSubscriber = locationSub;
        notifySubscriber(ConnectivityType.LOCATION);
    }

    public static void notifySubscriber(ConnectivityType type) {
        switch (type) {
            case NETWORK:
                if (networkSubscriber != null) {
                    networkSubscriber.update(networkStatus);
                }
                break;
            case BLUETOOTH:
                if (bluetoothSubscriber != null) {
                    bluetoothSubscriber.update(bluetoothStatus);
                }
                break;
            case LOCATION:
                if (locationSubscriber != null) {
                    locationSubscriber.update(locationStatus);
                }
                break;
        }
    }
}
