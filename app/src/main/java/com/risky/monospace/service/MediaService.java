package com.risky.monospace.service;

import com.risky.monospace.model.Media;
import com.risky.monospace.service.subscribers.MediaSubscriber;

public class MediaService extends MonoService<MediaSubscriber> {
    private static MediaService instance;
    private Media media;

    private MediaService() {
    }

    public static MediaService getInstance() {
        if (instance == null) {
            instance = new MediaService();
        }

        return instance;
    }

    public void set(Media media) {
        this.media = media;
        notifySubscriber();
    }

    @Override
    protected void updateSubscriber(MediaSubscriber subscriber) {
        subscriber.update(media);
    }
}
