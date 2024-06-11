package com.risky.monospace.service;

import com.risky.monospace.model.CalendarEvent;
import com.risky.monospace.service.subscribers.CalendarSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarService extends MonoService<CalendarSubscriber> {
    private List<CalendarEvent> events = new ArrayList<>();
    private static CalendarService instance;

    private CalendarService() {}

    public static CalendarService getInstance() {
        if (instance == null) {
            instance = new CalendarService();
        }

        return instance;
    }

    public void update(List<CalendarEvent> events) {
        this.events = events;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(CalendarSubscriber subscriber) {
        subscriber.update(Collections.unmodifiableList(events));
    }
}
