package com.risky.monospace.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.risky.monospace.R;

public abstract class MonoDialog extends Dialog {
    protected boolean isDestroyed = false;
    private final float dimAlpha;

    public MonoDialog(@NonNull Context context, int themeResId, float dimAlpha) {
        super(context, themeResId);

        this.dimAlpha = dimAlpha;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layout());
        getWindow().setDimAmount(dimAlpha);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
