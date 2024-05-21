package com.risky.monospace.dialog;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.DatePicker;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.receiver.TimeReceiver;
import com.risky.monospace.service.DialogService;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarDialog extends MonoDialog {
    private CalendarView calendar;
    private BroadcastReceiver timeReceiver;

    public CalendarDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.CALENDAR);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        getContext().unregisterReceiver(timeReceiver);

        // Avoid mem leak
        calendar = null;
    }

    @Override
    protected int layout() {
        return R.layout.calendar_dialog;
    }

    @Override
    protected void initialize() {
        calendar = findViewById(R.id.calendar_view);

        Runnable clockRunner = () -> {
            calendar.setDate(Calendar.getInstance().getTimeInMillis());
        };

        clockRunner.run();

        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver(clockRunner);
        getContext().registerReceiver(timeReceiver, timeFilter);
    }
}
