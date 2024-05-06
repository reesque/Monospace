package com.risky.monospace.model;

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.view.KeyEvent;

public class Media {
    private final MediaController controller;
    public final String packageName;
    public final String artist;
    public final String track;
    public final String album;
    public final Bitmap art;
    private boolean isPlaying;

    public Media(MediaController controller) {
        this.controller = controller;
        this.packageName = controller.getPackageName();
        this.artist = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_ARTIST);
        this.track = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE);
        this.album = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_ALBUM);
        this.art = controller.getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);

        isPlaying = false;
        if (controller.getPlaybackState() != null) {
            isPlaying = controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void sendMediaControl(KeyEvent event) {
        controller.dispatchMediaButtonEvent(event);
    }
}
