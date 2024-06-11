package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.CalendarEvent;

import java.util.List;

public interface CalendarSubscriber extends MonoSubscriber{
    void update(List<CalendarEvent> events);
}
