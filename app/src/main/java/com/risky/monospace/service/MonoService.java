package com.risky.monospace.service;

import com.risky.monospace.service.subscribers.MonoSubscriber;

import java.util.ArrayList;
import java.util.List;

public abstract class MonoService<T extends MonoSubscriber> {
    private final List<T> subscribers = new ArrayList<>();

    public void subscribe(T subscriber) {
        subscribers.add(subscriber);
        updateSubscriber(subscriber);
    }

    public void unsubscribe(T subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifySubscriber() {
        if (!subscribers.isEmpty()) {
            for (T subscriber : subscribers) {
                updateSubscriber(subscriber);
            }
        }
    }

    protected boolean noSubscriber() {
        return subscribers.isEmpty();
    }

    protected abstract void updateSubscriber(T subscriber);
}
