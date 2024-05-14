package com.risky.monospace.model;

import android.graphics.Bitmap;
import android.media.session.MediaController;

public class Media {
    public final String packageName;
    public final String artist;
    public final String track;
    public final String album;
    public final Bitmap coverArt;
    private final MediaController.TransportControls control;
    private boolean isPlaying;

    public Media(String artist, String track, String album, String packageName, Bitmap coverArt,
                 boolean isPlaying, MediaController.TransportControls control) {
        this.packageName = packageName;
        this.artist = artist;
        this.track = track;
        this.album = album;
        this.coverArt = coverArt;
        this.isPlaying = isPlaying;
        this.control = control;
    }

    public void play() {
        control.play();
        isPlaying = true;
    }

    public void pause() {
        control.pause();
        isPlaying = false;
    }

    public void next() {
        control.skipToNext();
    }

    public void previous() {
        control.skipToPrevious();
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
