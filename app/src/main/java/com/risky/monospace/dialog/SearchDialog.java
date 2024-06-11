package com.risky.monospace.dialog;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.model.AppListAdapter;
import com.risky.monospace.model.AppPackage;
import com.risky.monospace.model.SearchProvider;
import com.risky.monospace.service.AppPackageService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.subscribers.AppPackageSubscriber;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import kotlin.text.Charsets;

public class SearchDialog extends MonoDialog implements AppPackageSubscriber {
    private EditText searchBox;
    private AppListAdapter adapter;
    private ListView appList;
    private LinearLayout webSearchContainer;

    public SearchDialog(@NonNull Context context, int themeResId, float dimAlpha, boolean isFullscreen) {
        super(context, themeResId, dimAlpha, isFullscreen);
    }

    @Override
    protected int layout() {
        return R.layout.search_dialog;
    }

    @Override
    protected void initialize() {
        searchBox = findViewById(R.id.search_box);
        webSearchContainer = findViewById(R.id.web_container);
        appList = findViewById(R.id.app_list);

        AppPackageService.getInstance(getContext()).subscribe(this);

        webSearchContainer.setOnClickListener(v -> {
            String query = "";
            try {
                query = URLEncoder.encode(searchBox.getText().toString(), Charsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(SearchProvider.toProvider(
                    getContext().getSharedPreferences("settings", MODE_PRIVATE)
                            .getInt("searchProvider", 0)).baseUrl + query));
            new Handler(getMainLooper()).post(() -> getContext().startActivity(browserIntent));
            dismiss();
        });

        PackageManager pm = getContext().getPackageManager();
        appList.setOnItemClickListener((parent, view, position, id) -> {
            Intent launchIntent = pm.getLaunchIntentForPackage(
                    ((AppPackage) parent.getItemAtPosition(position)).packageName);
            new Handler(getMainLooper()).post(() -> getContext().startActivity(launchIntent));
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    webSearchContainer.setVisibility(View.VISIBLE);
                    appList.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(s);
                } else {
                    webSearchContainer.setVisibility(View.GONE);
                    appList.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        searchBox.requestFocus();
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.SEARCH);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppPackageService.getInstance(getContext()).unsubscribe(this);

        // Avoid mem leak
        searchBox = null;
        appList.setAdapter(null);
        appList = null;
        adapter = null;
        webSearchContainer = null;
    }

    @Override
    public void update(List<AppPackage> packages) {
        adapter = new AppListAdapter(getContext(), packages, R.layout.search_list_item);
        appList.setAdapter(adapter);
    }
}
