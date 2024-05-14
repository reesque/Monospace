package com.risky.monospace.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.risky.monospace.model.Media;
import com.risky.monospace.model.Notification;
import com.risky.monospace.service.MediaService;
import com.risky.monospace.service.NotificationService;

import java.util.List;

public class NotificationReceiver extends NotificationListenerService {
    public static final String NOTIFICATION_DISMISS_ACTION = "com.risky.dismiss_notification";
    public static final String NOTIFICATION_DISMISS_ALL_ACTION = "com.risky.dismiss_all_notification";
    public static final String EXTRA_NOTIFICATION_KEY = "notification_key";
    private static boolean isPermissionGranted = false;
    private final BroadcastReceiver internalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case NOTIFICATION_DISMISS_ACTION:
                        String notificationKey = intent.getStringExtra(EXTRA_NOTIFICATION_KEY);
                        cancelNotification(notificationKey);
                        break;
                    case NOTIFICATION_DISMISS_ALL_ACTION:
                        cancelAllNotifications();
                        break;
                }
            }
        }
    };

    public static boolean checkPermission() {
        return isPermissionGranted;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

        // Register the broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFICATION_DISMISS_ACTION);
        filter.addAction(NOTIFICATION_DISMISS_ALL_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(internalReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(internalReceiver, filter);
        }

        isPermissionGranted = true;

        mediaUpdate();

        for (StatusBarNotification sbn : getActiveNotifications()) {
            if (!sbn.isOngoing() && sbn.isClearable()
                    && (sbn.getNotification().flags
                    & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
                NotificationService.getInstance().add(notificationBuilder(sbn));
            }
        }
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        unregisterReceiver(internalReceiver);

        isPermissionGranted = false;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        mediaUpdate();

        if (!sbn.isOngoing() && sbn.isClearable()
                && (sbn.getNotification().flags
                & android.app.Notification.FLAG_GROUP_SUMMARY) == 0) {
            NotificationService.getInstance().add(notificationBuilder(sbn));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        mediaUpdate();

        NotificationService.getInstance().remove(notificationBuilder(sbn));
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

    private Notification notificationBuilder(StatusBarNotification sbn) {
        String title = sbn.getNotification().extras.getString(android.app.Notification.EXTRA_TITLE) == null ? "" :
                sbn.getNotification().extras.getString(android.app.Notification.EXTRA_TITLE).toString();
        String desc = sbn.getNotification().extras.get(android.app.Notification.EXTRA_TEXT) == null ? "" :
                sbn.getNotification().extras.get(android.app.Notification.EXTRA_TEXT).toString();

        return new Notification(sbn.getId(),
                sbn.getNotification().getSmallIcon().getResId(), sbn.getPackageName(), title, desc,
                sbn.getNotification().contentIntent, sbn.getKey());
    }
}
