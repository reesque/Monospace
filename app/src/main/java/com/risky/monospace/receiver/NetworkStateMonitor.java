package com.risky.monospace.receiver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.service.NetworkService;

import java.util.HashMap;
import java.util.Map;

public class NetworkStateMonitor extends ConnectivityManager.NetworkCallback {
    private final Context context;
    private final NetworkRequest networkRequest;
    private final Map<Network, Integer> networkMap;

    public NetworkStateMonitor(Context context) {
        this.context = context;
        this.networkMap = new HashMap<>();
        this.networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void register() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
        networkMap.clear();
    }

    public void unregister() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            networkMap.put(network, NetworkCapabilities.TRANSPORT_WIFI);
        } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            networkMap.put(network, NetworkCapabilities.TRANSPORT_CELLULAR);
        }

        updateState();
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);

        networkMap.remove(network);
        updateState();
    }

    private void updateState() {
        if (networkMap.containsValue(NetworkCapabilities.TRANSPORT_WIFI)) {
            NetworkService.getInstance().set(NetworkStatus.WIFI);
        } else if (networkMap.containsValue(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            NetworkService.getInstance().set(NetworkStatus.MOBILE_DATA);
        } else {
            NetworkService.getInstance().set(NetworkStatus.UNAVAILABLE);
        }
    }
}
