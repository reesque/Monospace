package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.risky.monospace.model.RegularPod;
import com.risky.monospace.model.SinglePod;
import com.risky.monospace.service.AirpodService;
import com.risky.monospace.util.AirpodBroadcastParam;

public class AirpodReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AirpodBroadcastParam.ACTION_STATUS)) {
            if (intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_IS_ALL_DISCONNECTED)) {
                AirpodService.getInstance().set(context, (SinglePod) null);
            }

            if (intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_IS_SINGLE)) {
                AirpodService.getInstance().set(context, new SinglePod(
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_MODEL),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_IS_ALL_DISCONNECTED),
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_SINGLE_POD_STATUS),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_SINGLE_POD_CHARGING)));
            } else {
                AirpodService.getInstance().set(context, new RegularPod(
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_MODEL),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_IS_ALL_DISCONNECTED),
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_LEFT_POD_STATUS),
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_RIGHT_POD_STATUS),
                        intent.getExtras().getString(AirpodBroadcastParam.EXTRA_POD_CASE_STATUS),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_LEFT_POD_CHARGING),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_RIGHT_POD_CHARGING),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_POD_CASE_CHARGING),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_LEFT_POD_IN_EAR),
                        intent.getExtras().getBoolean(AirpodBroadcastParam.EXTRA_RIGHT_POD_IN_EAR)
                ));
            }
        }
    }
}
