package com.risky.monospace.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;

import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.service.ConnectivityService;

public class NetworkStateMonitor extends ConnectivityManager.NetworkCallback {
    private Context context;
    private final NetworkRequest networkRequest;

    public NetworkStateMonitor(Context context) {
        this.context = context;
        this.networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
    }

    public void register() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(networkRequest, this);
    }

    public void unregister() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
        checkConnection();
    }

    @Override
    public void onLost(@NonNull Network network) {
        checkConnection();
    }

    private void checkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    ConnectivityService.set(NetworkStatus.WIFI);
                    return;
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    ConnectivityService.set(NetworkStatus.MOBILE_DATA);
                    return;
                }
            }
        }

        ConnectivityService.set(NetworkStatus.UNAVAILABLE);
    }
}
