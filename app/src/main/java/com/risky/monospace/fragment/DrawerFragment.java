package com.risky.monospace.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.os.Looper.getMainLooper;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.risky.monospace.R;
import com.risky.monospace.model.AppListAdapter;
import com.risky.monospace.model.AppPackage;
import com.risky.monospace.service.AppPackageService;
import com.risky.monospace.service.subscribers.AppPackageSubscriber;

import java.util.List;

public class DrawerFragment extends Fragment implements AppPackageSubscriber {
    private AppListAdapter adapter;
    private GridView appList;

    public DrawerFragment() {
        // Empty
    }

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.drawer_fragment, container, false);

        appList = view1.findViewById(R.id.app_list);

        // ### App list ###
        AppPackageService.getInstance(getContext()).subscribe(this);

        PackageManager pm = getContext().getPackageManager();
        appList.setOnItemClickListener((parent, view, position, id) -> {
            Intent launchIntent = pm.getLaunchIntentForPackage(
                    ((AppPackage) parent.getItemAtPosition(position)).packageName);
            new Handler(getMainLooper()).post(() -> startActivity(launchIntent));
        });

        appList.setOnItemLongClickListener((parent, view, position, id) -> {
            AppPackageService.getInstance(getContext()).toggleFav(getContext(),
                    ((AppPackage) parent.getItemAtPosition(position)));
            return true;
        });

        return view1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppPackageService.getInstance(getContext()).unsubscribe(this);

        // Avoid mem leak
        appList.setAdapter(null);
        adapter = null;
        appList = null;
    }

    @Override
    public void update(List<AppPackage> packages) {
        adapter = new AppListAdapter(getContext(), packages, R.layout.app_list_item);
        appList.setAdapter(adapter);
    }
}
