package com.risky.monospace.receiver;

import android.content.ComponentName;
import android.content.Context;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
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
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();

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

                MediaService.getInstance().set(new Media(controller));
                return;
            }
        }

        MediaService.getInstance().set(null);
    }
}
