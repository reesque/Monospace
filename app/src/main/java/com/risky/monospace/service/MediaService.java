package com.risky.monospace.service;

import com.risky.monospace.model.Media;
import com.risky.monospace.service.subscribers.MediaSubscriber;

public class MediaService {
    private static Media media;
    private static MediaSubscriber subscriber;

    public static void set(Media m) {
        media = m;
        notifySubscriber();
    }

    public static void subscribe(MediaSubscriber sub) {
        subscriber = sub;
        notifySubscriber();
    }

    public static void notifySubscriber() {
        if (subscriber != null) {
            subscriber.update(media);
        }
    }
}
