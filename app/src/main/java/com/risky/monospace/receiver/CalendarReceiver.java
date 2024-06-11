package com.risky.monospace.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.risky.monospace.model.CalendarEvent;
import com.risky.monospace.service.CalendarService;
import com.risky.monospace.util.PermissionHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarReceiver extends BroadcastReceiver {
    public CalendarReceiver() {}
    public CalendarReceiver(Context context) {
        if (PermissionHelper.checkCalendar(context)) {
            update(context);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        update(context);
    }

    private void update(Context context) {
        Calendar now = Calendar.getInstance();
        Calendar thirtyDays = Calendar.getInstance();
        thirtyDays.add(Calendar.DATE, 30);

        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://com.android.calendar/events"),
                new String[] {"title", "description", "dtstart", "dtend", "eventLocation" },"dtstart >= ? AND dtstart <= ?",
                new String[]{String.valueOf(now.getTimeInMillis()), String.valueOf(thirtyDays.getTimeInMillis())}, "dtstart ASC");
        cursor.moveToFirst();

        List<CalendarEvent> events = new ArrayList<>();
        for (int i = 0; i < cursor.getCount(); i++) {
            Calendar endTime = cursor.getString(3) == null ?
                    null : getDate(Long.parseLong(cursor.getString(3)));

            events.add(new CalendarEvent(cursor.getString(0),
                    cursor.getString(1), cursor.getString(4),
                    getDate(Long.parseLong(cursor.getString(2))), endTime));
            cursor.moveToNext();
        }

        cursor.close();
        CalendarService.getInstance().update(events);
    }

    private Calendar getDate(long milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return calendar;
    }
}
