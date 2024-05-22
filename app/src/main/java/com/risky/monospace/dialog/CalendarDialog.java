package com.risky.monospace.dialog;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.receiver.TimeReceiver;
import com.risky.monospace.service.AlarmService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.subscribers.AlarmSubscriber;
import com.risky.monospace.util.DTFormattertUtil;

import java.util.Calendar;
import java.util.TimeZone;

public class CalendarDialog extends MonoDialog {
    private CalendarView calendar;
    private Calendar checkpointDate;
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
            Calendar current = Calendar.getInstance();
            if (checkpointDate == null || checkpointDate.get(Calendar.YEAR) != current.get(Calendar.YEAR)
                    || checkpointDate.get(Calendar.MONTH) != current.get(Calendar.MONTH)
                    || checkpointDate.get(Calendar.DAY_OF_MONTH) != current.get(Calendar.DAY_OF_MONTH)) {
                checkpointDate = current;
                calendar.setDate(current.getTimeInMillis());
            }
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
