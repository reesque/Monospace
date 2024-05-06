package com.risky.monospace.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.risky.monospace.model.BluetoothStatus;
import com.risky.monospace.service.BluetoothService;
import com.risky.monospace.service.NetworkService;

public class BluetoothReceiver extends BroadcastReceiver {
    public BluetoothReceiver() {
        BluetoothService.getInstance().set(BluetoothAdapter.getDefaultAdapter().isEnabled()
                ? BluetoothStatus.ON : BluetoothStatus.UNAVAILABLE);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                case BluetoothAdapter.STATE_TURNING_OFF:
                    BluetoothService.getInstance().set(BluetoothStatus.UNAVAILABLE);
                    break;
                case BluetoothAdapter.STATE_ON:
                case BluetoothAdapter.STATE_TURNING_ON:
                    BluetoothService.getInstance().set(BluetoothStatus.ON);
                    break;
            }
        }
    }
}
