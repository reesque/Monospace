package com.risky.monospace.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.service.DialogService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kotlin.text.Charsets;

public class SearchDialog extends MonoDialog {
    private EditText searchBox;
    private ImageView searchButton;

    public SearchDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected int layout() {
        return R.layout.search_dialog;
    }

    @Override
    protected void initialize() {
        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);

        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search();
                return true;
            }

            return false;
        });

        searchButton.setOnClickListener(v -> search());
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.SEARCH);

        super.dismiss();

        // Avoid mem leak
        searchBox = null;
        searchButton = null;
    }

    private void search() {
        if (!searchBox.getText().toString().isEmpty()) {
            String query = "";
            try {
                query = URLEncoder.encode(searchBox.getText().toString(), Charsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                return;
            }

            DialogService.getInstance().show(getContext(), DialogType.WEB,
                    "https://www.google.com/search?igu=1&q=" + query);
            dismiss();
        }
    }
}
