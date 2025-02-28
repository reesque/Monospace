package com.risky.monospace.model;

import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.recyclerview.widget.RecyclerView;

import com.risky.monospace.MainActivity;
import com.risky.monospace.R;
import com.risky.monospace.fragment.GreetFragment;
import com.risky.monospace.service.AppPackageService;

import java.util.List;

public class ContentPagerAdapter extends RecyclerView.Adapter<ContentPagerAdapter.PageViewHolder> {
    private List<List<AppPackage>> pages;
    private Context context;

    public ContentPagerAdapter(Context context, List<List<AppPackage>> pages) {
        this.pages = pages;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.greet_fragment_container, parent, false);

            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GreetFragment.newInstance())
                    .commit();
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_fragment, parent, false);
        }

        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        if (position != 0) {
            GridView gridView = holder.itemView.findViewById(R.id.app_list);
            gridView.setAdapter(new AppListAdapter(holder.itemView.getContext(), pages.get(position - 1), R.layout.app_list_item));

            PackageManager pm = context.getPackageManager();
            gridView.setOnItemClickListener((parent, view, pos, id) -> {
                Intent launchIntent = pm.getLaunchIntentForPackage(
                        ((AppPackage) parent.getItemAtPosition(pos)).packageName);
                new Handler(getMainLooper()).post(() -> context.startActivity(launchIntent));
            });

            gridView.setOnItemLongClickListener((parent, view, pos, id) -> {
                AppPackageService.getInstance(context).toggleFav(context,
                        ((AppPackage) parent.getItemAtPosition(pos)));
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return pages.size() + 1;
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        public PageViewHolder(View itemView) {
            super(itemView);
        }
    }
}
