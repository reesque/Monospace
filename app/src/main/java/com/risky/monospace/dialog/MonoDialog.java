package com.risky.monospace.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

public class MonoDialog extends Dialog {
    protected boolean isDestroyed = false;

    public MonoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isDestroyed = true;
    }
}
