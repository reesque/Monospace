package com.risky.monospace.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
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
    private View view;
    private AppListAdapter adapter;
    private GridView appList;
    private EditText appSearch;
    private LinearLayout searchBar;

    public DrawerFragment() {
        // Empty
    }

    public static DrawerFragment newInstance() {
        return new DrawerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drawer_fragment, container, false);

        appList = view.findViewById(R.id.app_list);
        appSearch = view.findViewById(R.id.app_search_edit);
        searchBar = view.findViewById(R.id.drawer_search_bar);

        // ### App list ###
        AppPackageService.getInstance().subscribe(this);

        // ### Search app ###
        PackageManager pm = getContext().getPackageManager();
        appList.setOnItemClickListener((parent, view, position, id) -> {
            String pname = ((AppPackage) parent.getItemAtPosition(position)).packageName;
            Intent launchIntent = pm.getLaunchIntentForPackage(pname);
            startActivity(launchIntent);
        });

        appList.setOnItemLongClickListener((parent, view, position, id) -> {
            String pname = ((AppPackage) parent.getItemAtPosition(position)).packageName;
            AppPackageService.getInstance().toggleFav(getContext(), pname);
            return true;
        });

        appSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Make sure keyboard can popup more reliably
        searchBar.setOnClickListener(v -> {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(appSearch.findFocus(), 0);
        });

        appSearch.requestFocus();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppPackageService.getInstance().unsubscribe(this);
    }

    @Override
    public void update(List<AppPackage> packages) {
        adapter = new AppListAdapter(getContext(), packages);
        appList.setAdapter(adapter);
    }
}
