package com.risky.monospace.model;

import java.util.Calendar;

public class CalendarEvent {
    public final String name;
    public final String description;
    public final String location;
    public final Calendar start;
    public final Calendar end;

    public CalendarEvent(String name, String description, String location, Calendar start, Calendar end) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.start = start;
        this.end = end;
    }
}
