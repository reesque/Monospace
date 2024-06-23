package com.risky.monospace.dialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.CalendarView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.CalendarEvent;
import com.risky.monospace.model.CalendarEventListAdapter;
import com.risky.monospace.receiver.TimeReceiver;
import com.risky.monospace.service.CalendarService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.subscribers.CalendarSubscriber;

import java.util.Calendar;
import java.util.List;

public class CalendarDialog extends MonoDialog implements CalendarSubscriber {
    private CalendarView calendar;
    private Calendar checkpointDate;
    private ListView eventList;
    private BroadcastReceiver timeReceiver;
    private CalendarEventListAdapter adapter;

    public CalendarDialog(@NonNull Context context, int themeResId, float dimAlpha, boolean isFullscreen) {
        super(context, themeResId, dimAlpha, isFullscreen);
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
        CalendarService.getInstance().unsubscribe(this);

        // Avoid mem leak
        eventList.setAdapter(null);
        adapter = null;
        calendar = null;
        eventList = null;
    }

    @Override
    protected int layout() {
        return R.layout.calendar_dialog;
    }

    @Override
    protected void initialize() {
        calendar = findViewById(R.id.calendar_view);
        eventList = findViewById(R.id.calendar_event_list);
        eventList.setEmptyView(findViewById(R.id.empty_view_list));

        findViewById(R.id.empty_view_list).setOnClickListener(v -> dismiss());
        findViewById(R.id.dialog_pane).setOnClickListener(v -> dismiss());

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

        CalendarService.getInstance().subscribe(this);
    }

    @Override
    public void update(List<CalendarEvent> events) {
        if (isDestroyed) {
            return;
        }

        adapter = new CalendarEventListAdapter(getContext(), events);
        eventList.setAdapter(adapter);
    }
}
