package com.risky.monospace.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.Media;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.subscribers.MediaSubscriber;

public class MediaDialog extends MonoDialog implements MediaSubscriber {
    private ImageView mediaArt;
    private TextView album;
    private TextView track;
    private TextView artist;
    private ImageView prev;
    private ImageView play;
    private ImageView next;

    public MediaDialog(@NonNull Context context, int themeResId, float dimAlpha) {
        super(context, themeResId, dimAlpha);
    }

    @Override
    public void update(Media media) {
        if (isDestroyed) {
            return;
        }

        if (media != null) {
            if (media.coverArt != null) {
                mediaArt.setImageBitmap(media.coverArt);
            } else {
                mediaArt.setImageBitmap(null);
            }

            if (media.album != null) {
                album.setText(media.album);
            } else {
                album.setText(getContext().getString(R.string.widget_media_unknown));
            }

            if (media.track != null) {
                track.setText(media.track);
            } else {
                track.setText(getContext().getString(R.string.widget_media_unknown));
            }

            if (media.artist != null) {
                artist.setText(media.artist);
            } else {
                artist.setText(getContext().getString(R.string.widget_media_unknown));
            }

            if (media.isPlaying()) {
                play.setImageResource(R.drawable.pause);
            } else {
                play.setImageResource(R.drawable.play);
            }

            prev.setOnClickListener(v -> media.previous());

            play.setOnClickListener(v -> {
                if (media.isPlaying()) {
                    media.pause();
                } else {
                    media.play();
                }
            });

            next.setOnClickListener(v -> media.next());

            return;
        }

        mediaArt.setImageBitmap(null);
        album.setText(getContext().getString(R.string.widget_media_unknown));
        track.setText(getContext().getString(R.string.widget_media_unknown));
        artist.setText(getContext().getString(R.string.widget_media_unknown));

        prev.setOnClickListener(null);
        play.setOnClickListener(null);
        next.setOnClickListener(null);

        play.setImageResource(R.drawable.play);
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.MEDIA);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        MediaService.getInstance().unsubscribe(this);

        // Avoid mem leak
        mediaArt = null;
        album = null;
        track = null;
        artist = null;
        prev = null;
        play = null;
        next = null;
    }

    @Override
    protected int layout() {
        return R.layout.media_control_dialog;
    }

    @Override
    protected void initialize() {
        mediaArt = findViewById(R.id.media_art);
        album = findViewById(R.id.media_album);
        track = findViewById(R.id.media_track);
        artist = findViewById(R.id.media_artist);
        prev = findViewById(R.id.media_prev);
        play = findViewById(R.id.media_play);
        next = findViewById(R.id.media_next);

        MediaService.getInstance().subscribe(this);

        // Funny scrolling title
        album.setSelected(true);
        track.setSelected(true);
        artist.setSelected(true);
    }
}
