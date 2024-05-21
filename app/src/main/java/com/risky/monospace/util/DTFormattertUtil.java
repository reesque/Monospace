package com.risky.monospace.util;

import java.text.SimpleDateFormat;

public class DTFormattertUtil {
    public static final SimpleDateFormat alarmDisplay = new SimpleDateFormat("EEE hh:mm a");
    public static final SimpleDateFormat alarmDisplayFull = new SimpleDateFormat("EEE MMM dd yyyy hh:mm a");
    public static final SimpleDateFormat dayOfMonth = new SimpleDateFormat("dd");
    public static final SimpleDateFormat time = new SimpleDateFormat("hh:mm");
    public static final SimpleDateFormat meridiem = new SimpleDateFormat("a");
    public static final SimpleDateFormat month = new SimpleDateFormat("MMM");
}
