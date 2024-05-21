package com.risky.monospace.service;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.risky.monospace.R;
import com.risky.monospace.dialog.AirpodDialog;
import com.risky.monospace.dialog.CalendarDialog;
import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.dialog.MediaDialog;
import com.risky.monospace.dialog.NotificationDialog;
import com.risky.monospace.dialog.SearchDialog;
import com.risky.monospace.dialog.WeatherDialog;
import com.risky.monospace.dialog.WebDialog;

import java.util.HashMap;
import java.util.Map;

public class DialogService {
    private static DialogService instance;
    private final Map<DialogType, Dialog> active = new HashMap<>();

    private DialogService() {
    }

    public static DialogService getInstance() {
        if (instance == null) {
            instance = new DialogService();
        }

        return instance;
    }

    public void show(Context context, DialogType type, String arg) {
        if (active.get(type) != null) {
            return;
        }

        Dialog dialog = null;
        switch (type) {
            case AIRPOD:
                dialog = new AirpodDialog(context, R.style.MonoDialog);
                break;
            case MEDIA:
                dialog = new MediaDialog(context, R.style.MonoDialog);
                break;
            case WEATHER:
                dialog = new WeatherDialog(context, R.style.MonoDialog);
                break;
            case NOTIFICATION:
                dialog = new NotificationDialog(context, R.style.MonoDialog);
                break;
            case SEARCH:
                dialog = new SearchDialog(context, R.style.MonoDialog);
                break;
            case WEB:
                dialog = new WebDialog(context, R.style.MonoDialog, arg);
                break;
            case CALENDAR:
                dialog = new CalendarDialog(context, R.style.MonoDialog);
                break;
        }

        Window window = dialog.getWindow();
        window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        window.setGravity(Gravity.BOTTOM);
        window.getAttributes().windowAnimations = R.style.MonoDialogAnimation;
        active.put(type, dialog);
        dialog.show();
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
