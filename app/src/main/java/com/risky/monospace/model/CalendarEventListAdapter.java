package com.risky.monospace.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.util.DTFormattertUtil;

import java.util.ArrayList;
import java.util.List;

public class CalendarEventListAdapter extends ArrayAdapter<CalendarEvent> implements Filterable {
    public CalendarEventListAdapter(Context context, List<CalendarEvent> events) {
        super(context, 0, new ArrayList<>(events));
    }

    public CalendarEventListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CalendarEvent item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.calendar_event_list_item, parent, false);
        }

        TextView dom = convertView.findViewById(R.id.calendar_day);
        dom.setText(DTFormattertUtil.dayOfMonth.format(item.start.getTime()));

        TextView month = convertView.findViewById(R.id.calendar_month);
        month.setText(DTFormattertUtil.month.format(item.start.getTime()));

        TextView time = convertView.findViewById(R.id.calendar_time);
        if (item.end == null) {
            time.setText(DTFormattertUtil.time.format(item.start.getTime()));
        } else {
            time.setText(DTFormattertUtil.time.format(item.start.getTime()) + " - "
                    + DTFormattertUtil.time.format(item.end.getTime()));
        }

        TextView name = convertView.findViewById(R.id.calendar_header);
        name.setText(item.name);

        TextView desc = convertView.findViewById(R.id.calendar_desc);
        if (item.description == null || item.description.isEmpty()) {
            desc.setText(getContext().getString(R.string.widget_event_no_desc));
        } else {
            desc.setText(item.description);
        }

        TextView location = convertView.findViewById(R.id.calendar_location);
        if (item.location == null || item.location.isEmpty()) {
            location.setText(getContext().getString(R.string.widget_event_no_location));
        } else {
            location.setText(getContext().getString(R.string.widget_event_at) + " " + item.location);
        }

        return convertView;
    }
}
