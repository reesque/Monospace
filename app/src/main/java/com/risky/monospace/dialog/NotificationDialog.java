package com.risky.monospace.dialog;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.Notification;
import com.risky.monospace.model.NotificationListAdapter;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.service.subscribers.NotificationSubscriber;

import java.util.List;

public class NotificationDialog extends Dialog implements NotificationSubscriber {
    private ListView notificationList;
    private NotificationListAdapter adapter;

    public NotificationDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.notification_dialog);

        notificationList = findViewById(R.id.notification_list);
        notificationList.setEmptyView(findViewById(R.id.empty_view_list));

        notificationList.setOnItemClickListener((parent, view, position, id) -> {
            ((Notification) parent.getItemAtPosition(position)).onClick();
        });

        NotificationService.getInstance().subscribe(this);
    }

    @Override
    public void update(List<Notification> notifications) {
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
    }
}