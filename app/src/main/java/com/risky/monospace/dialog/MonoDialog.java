package com.risky.monospace.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

public abstract class MonoDialog extends Dialog {
    protected boolean isDestroyed = false;
    private final float dimAlpha;
    private final boolean isFullscreen;

    public MonoDialog(@NonNull Context context, int themeResId, float dimAlpha, boolean isFullscreen) {
        super(context, themeResId);

        this.dimAlpha = dimAlpha;
        this.isFullscreen = isFullscreen;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layout());
        getWindow().setDimAmount(dimAlpha);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (isFullscreen) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        initialize();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroyed = true;
    }

    @LayoutRes
    protected abstract int layout();

    protected abstract void initialize();
}
