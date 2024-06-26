package com.risky.monospace.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.Notification;
import com.risky.monospace.model.NotificationListAdapter;
import com.risky.monospace.receiver.NotificationReceiver;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.service.subscribers.NotificationSubscriber;

import java.util.List;

public class NotificationDialog extends MonoDialog implements NotificationSubscriber {
    private ListView notificationList;
    private TextView dismissAllButton;
    private NotificationListAdapter adapter;

    public NotificationDialog(@NonNull Context context, int themeResId, float dimAlpha, boolean isFullscreen) {
        super(context, themeResId, dimAlpha, isFullscreen);
    }

    @Override
    public void update(List<Notification> notifications) {
        if (isDestroyed) {
            return;
        }

        if (notifications.isEmpty()) {
            dismissAllButton.setVisibility(View.GONE);
        } else {
            dismissAllButton.setVisibility(View.VISIBLE);
        }

        adapter = new NotificationListAdapter(getContext(), notifications);
        notificationList.setAdapter(adapter);
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.NOTIFICATION);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        NotificationService.getInstance().unsubscribe(this);

        // Avoid mem leak
        notificationList.setAdapter(null);
        adapter = null;
        notificationList = null;
        dismissAllButton = null;
    }

    @Override
    protected int layout() {
        return R.layout.notification_dialog;
    }

    @Override
    protected void initialize() {
        notificationList = findViewById(R.id.notification_list);
        notificationList.setEmptyView(findViewById(R.id.empty_view_list));
        dismissAllButton = findViewById(R.id.notification_dismiss_button);

        findViewById(R.id.empty_view_list).setOnClickListener(v -> dismiss());
        findViewById(R.id.dialog_pane).setOnClickListener(v -> dismiss());

        notificationList.setOnItemClickListener((parent, view, position, id)
                -> ((Notification) parent.getItemAtPosition(position)).expand(getContext()));

        notificationList.setOnItemLongClickListener((parent, view, position, id) -> {
            ((Notification) parent.getItemAtPosition(position)).clear(getContext());
            return true;
        });

        dismissAllButton.setOnClickListener(v -> {
            Intent intent = new Intent(NotificationReceiver.NOTIFICATION_DISMISS_ALL_ACTION);
            getContext().sendBroadcast(intent);
            NotificationService.getInstance().removeAll();
        });

        NotificationService.getInstance().subscribe(this);
    }
}
