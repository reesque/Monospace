package com.risky.monospace.service.subscribers;

import com.risky.monospace.model.Media;

public interface MediaSubscriber extends MonoSubscriber {
    void update(Media media);
}
