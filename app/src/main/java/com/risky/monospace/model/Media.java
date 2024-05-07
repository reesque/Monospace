package com.risky.monospace.model;

import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.util.Log;
import android.view.KeyEvent;

public class Media {
    public final String packageName;
    public final String artist;
    public final String track;

    public Media(String artist, String track, String packageName) {
        this.packageName = packageName;
        this.artist = artist;
        this.track = track;
    }
}
