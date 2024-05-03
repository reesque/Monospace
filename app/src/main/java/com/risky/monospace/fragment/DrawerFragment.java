package com.risky.monospace.fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.risky.monospace.R;
import com.risky.monospace.model.AppListAdapter;
import com.risky.monospace.model.AppPackage;
import com.risky.monospace.service.AppPackageService;
import com.risky.monospace.util.PacManSubscriber;

import java.util.List;

public class DrawerFragment extends Fragment implements PacManSubscriber {
    private View view;
    private Context context;
    private InputMethodManager imm;
    private AppListAdapter adapter;
    private ListView appList;
    private EditText appSearch;
    private LinearLayout searchBar;

    public DrawerFragment(Context context) {
        this.context = context;
        imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.drawer_fragment, container, false);

        appList = view.findViewById(R.id.app_list);
        appSearch = view.findViewById(R.id.app_search_edit);
        searchBar = view.findViewById(R.id.drawer_search_bar);

        // ### App list ###
        AppPackageService.subscribe(this);

        // ### Search app ###
        PackageManager pm = context.getPackageManager();
        appList.setOnItemClickListener((parent, view, position, id) -> {
            String pname = ((AppPackage) parent.getItemAtPosition(position)).packageName;
            Intent launchIntent = pm.getLaunchIntentForPackage(pname);
            startActivity(launchIntent);
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

        appSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE){
                appSearch.clearFocus();
                imm.hideSoftInputFromWindow(appSearch.getWindowToken(), 0);

                return true;
            }
            return false;
        });

        // Make sure keyboard can popup more reliably
        searchBar.setOnClickListener(v -> {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(appSearch.findFocus(), 0);
        });

        appSearch.requestFocus();

        return view;
    }

    @Override
    public void update(List<AppPackage> packages) {
        adapter = new AppListAdapter(context, packages);
        appList.setAdapter(adapter);
    }
}
