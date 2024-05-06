package com.risky.monospace.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.Media;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.subscribers.MediaSubscriber;

public class MediaDialog extends Dialog implements MediaSubscriber {
    private ImageView mediaArt;
    private TextView album;
    private TextView track;
    private TextView artist;
    private ImageView prev;
    private ImageView play;
    private ImageView next;

    
    public MediaDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.media_control_dialog);

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

    @Override
    public void update(Media media) {
        if (media != null) {
            if (media.art != null) {
                mediaArt.setImageBitmap(media.art);
            } else {
                mediaArt.setImageBitmap(null);
            }

            if (media.album != null) {
                album.setText(media.album);
            } else {
                album.setText("Unknown");
            }

            if (media.track != null) {
                track.setText(media.track);
            } else {
                track.setText("Unknown");
            }

            if (media.artist != null) {
                artist.setText(media.artist);
            } else {
                artist.setText("Unknown");
            }

            if(media.isPlaying()) {
                play.setImageResource(R.drawable.pause);
            } else {
                play.setImageResource(R.drawable.play);
            }

            prev.setOnClickListener(v -> {
                    KeyEvent downEvent =
                            new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
                    media.sendMediaControl(downEvent);
            });

            play.setOnClickListener(v -> {
                KeyEvent downEvent;

                if (media.isPlaying()) {
                    downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE);
                } else {
                    downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY);
                }
                media.sendMediaControl(downEvent);
            });

            next.setOnClickListener(v -> {
                KeyEvent downEvent =
                        new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
                media.sendMediaControl(downEvent);
            });

            return;
        }

        mediaArt.setImageBitmap(null);
        album.setText("Unknown");
        track.setText("Unknown");
        artist.setText("Unknown");

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
    }
}
