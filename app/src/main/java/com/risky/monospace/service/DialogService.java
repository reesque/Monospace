package com.risky.monospace.service;

import android.app.Dialog;
import android.content.Context;

import com.risky.monospace.dialog.AirpodDialog;
import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.dialog.MediaDialog;
import com.risky.monospace.dialog.WeatherDialog;

import java.util.HashMap;
import java.util.Map;

public class DialogService {
    private static DialogService instance;
    private final Map<DialogType, Dialog> active = new HashMap<>();

    private DialogService() {}

    public static DialogService getInstance() {
        if (instance == null) {
            instance = new DialogService();
        }

        return instance;
    }

    public void show(Context context, DialogType type) {
        switch (type) {
            case AIRPOD:
                active.put(type, new AirpodDialog(context));
                active.get(type).show();
                break;
            case MEDIA:
                active.put(type, new MediaDialog(context));
                active.get(type).show();
                break;
            case WEATHER:
                active.put(type, new WeatherDialog(context));
                active.get(type).show();
                break;
        }
    }

    public void dismiss(DialogType type) {
        if (active.get(type) != null && active.get(type).isShowing()) {
            active.get(type).dismiss();
            cancel(type);
        }
    }

    public void cancel(DialogType type) {
        active.put(type, null);
    }

    public void dismissAll() {
        for (Dialog d : active.values()) {
            if (d != null && d.isShowing()) {
                d.dismiss();
            }
        }

        active.clear();
    }
}
