package com.risky.simplify.model;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.risky.simplify.R;
import com.risky.simplify.model.AppListItem;
import com.risky.simplify.service.AppCacheService;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppListItem> implements Filterable {
    private List<AppListItem> originalItems;

    public AppListAdapter(Context context) {
        super(context, 0, new ArrayList<>(AppCacheService.get()));
        originalItems = new ArrayList<>(AppCacheService.get());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppListItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_list_item, parent, false);
        }

        TextView name = convertView.findViewById(R.id.app_name);
        name.setText(item.name);

        ImageView icon = convertView.findViewById(R.id.app_icon);
        icon.setImageDrawable(item.icon);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        icon.setColorFilter(cf);

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<AppListItem> filteredArrayNames = new ArrayList<>();

                results.count = originalItems.size();
                results.values = originalItems;

                if (constraint != null && !constraint.toString().isEmpty()) {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalItems.size(); i++) {
                        if (originalItems.get(i).name.toLowerCase().contains(constraint) ||
                                originalItems.get(i).packageName.toLowerCase().contains(constraint)) {
                            filteredArrayNames.add(originalItems.get(i));
                        }
                    }

                    results.count = filteredArrayNames.size();
                    results.values = filteredArrayNames;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {clear();
                clear();
                if (results.count != 0) {
                    addAll((List<AppListItem>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
}
