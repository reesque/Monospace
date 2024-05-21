package com.risky.monospace.receiver;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.risky.monospace.service.AlarmService;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {}

    public AlarmReceiver(Context context) {
        checkForAvaialbleAlarm(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED)) {
            checkForAvaialbleAlarm(context);
        }
    }

    private void checkForAvaialbleAlarm(Context context) {
        Calendar nextAlarm = null;
        AlarmManager.AlarmClockInfo alarmInfo = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE)).getNextAlarmClock();
        if (alarmInfo != null) {
            nextAlarm = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            nextAlarm.setTimeInMillis(alarmInfo.getTriggerTime());
            nextAlarm.setTimeZone(TimeZone.getDefault());
        }

        AlarmService.getInstance().set(nextAlarm);
    }
}
