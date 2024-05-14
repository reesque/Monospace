package com.risky.monospace.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.risky.monospace.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationListAdapter extends ArrayAdapter<Notification> implements Filterable {
    public NotificationListAdapter(Context context, List<Notification> notifications) {
        super(context, 0, new ArrayList<>(notifications));
    }

    public NotificationListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_item, parent, false);
        }

        try {
            Resources res = getContext().getPackageManager()
                    .getResourcesForApplication(item.packageName);
            Drawable iconDrawable = ResourcesCompat.getDrawable(res, item.icon, null);

            ImageView icon = convertView.findViewById(R.id.notification_icon);
            icon.setImageDrawable(iconDrawable);
            icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        } catch (PackageManager.NameNotFoundException e) {
            // No need to handle. This application simply doesn't have an icon.
        }

        TextView header = convertView.findViewById(R.id.notification_header);
        header.setText(item.title);

        TextView desc = convertView.findViewById(R.id.notification_desc);
        if (item.description.isEmpty()) {
            desc.setVisibility(View.GONE);
        } else {
            desc.setVisibility(View.VISIBLE);
            desc.setText(item.description);
        }

        return convertView;
    }
}
