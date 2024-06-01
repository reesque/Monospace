package com.risky.monospace.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.risky.monospace.R;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppPackage> implements Filterable {
    private final List<AppPackage> originalItems;
    private final int itemLayout;

    public AppListAdapter(Context context, List<AppPackage> packages, int itemLayout) {
        super(context, 0, new ArrayList<>(packages));
        this.originalItems = new ArrayList<>(packages);
        this.itemLayout = itemLayout;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppPackage item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(itemLayout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.app_name);
        name.setText(item.name);

        ImageView favIcon = convertView.findViewById(R.id.app_fav);
        favIcon.setVisibility(item.isFav() ? View.VISIBLE : View.INVISIBLE);

        ShapeableImageView icon = convertView.findViewById(R.id.app_icon);
        icon.setImageDrawable(item.icon);
        icon.setContentPadding(4, 4, 4, 4);
        icon.setShapeAppearanceModel(icon.getShapeAppearanceModel()
                .toBuilder().setAllCorners(CornerFamily.ROUNDED, 50f).build());

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<AppPackage> filteredArrayNames = new ArrayList<>();

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
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                clear();
                if (results.count != 0) {
                    addAll((List<AppPackage>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }
}
