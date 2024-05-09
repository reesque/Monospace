package com.risky.monospace.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;

import com.risky.monospace.model.Media;
import com.risky.monospace.model.Notification;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.NotificationService;

import java.util.List;

public class NotificationReceiver extends NotificationListenerService {
    private static boolean isPermissionGranted = false;

    public static boolean checkPermission() {
        return isPermissionGranted;
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        isPermissionGranted = true;

        mediaUpdate();

        for (StatusBarNotification sbn : getActiveNotifications()) {
            if (sbn.getNotification().category != null
                    && (sbn.getNotification().flags
                    & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
                NotificationService.getInstance().add(new Notification(sbn.getId(),
                        sbn.getNotification().getSmallIcon().getResId(),
                        sbn.getPackageName()));
            }
        }
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();

        isPermissionGranted = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        mediaUpdate();

        if (sbn.getNotification().category != null
                && (sbn.getNotification().flags
                & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
            NotificationService.getInstance().add(new Notification(sbn.getId(),
                    sbn.getNotification().getSmallIcon().getResId(),
                    sbn.getPackageName()));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        mediaUpdate();

        NotificationService.getInstance().remove(new Notification(sbn.getId(),
                sbn.getNotification().getSmallIcon().getResId(),
                sbn.getPackageName()));
    }

    private void mediaUpdate() {
        MediaSessionManager msm = (MediaSessionManager) getApplication().getSystemService(Context.MEDIA_SESSION_SERVICE);
        List<MediaController> sessions = msm.getActiveSessions(new ComponentName(this, NotificationReceiver.class));

        if (!sessions.isEmpty()) {
            MediaController controller = sessions.get(0);
            if (controller != null && controller.getMetadata() != null) {
                String artist = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_ARTIST);
                String track = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE);
                String album = controller.getMetadata().getString(MediaMetadata.METADATA_KEY_ALBUM);
                Bitmap coverArt = controller.getMetadata().getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART);
                boolean isPlaying = false;
                if (controller.getPlaybackState() != null) {
                    isPlaying = controller.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
                }
                MediaController.TransportControls control = controller.getTransportControls();

                if (artist == null && track == null) {
                    MediaService.getInstance().set(null);
                    return;
                }

                MediaService.getInstance().set(new Media(artist, track, album, controller.getPackageName(), coverArt, isPlaying, control));
                return;
            }
        }

        MediaService.getInstance().set(null);
    }
}
