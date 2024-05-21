package com.risky.monospace.dialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.risky.monospace.R;
import com.risky.monospace.service.DialogService;

public class WebDialog extends MonoDialog {
    private final String baseUrl;
    private WebView webView;
    private ProgressBar loadBar;

    public WebDialog(@NonNull Context context, int themeResId, String baseUrl) {
        super(context, themeResId);
        this.baseUrl = baseUrl;
    }

    @Override
    public void dismiss() {
        DialogService.getInstance().cancel(DialogType.WEB);

        super.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Avoid mem leak
        webView = null;
        loadBar = null;
    }

    @Override
    protected int layout() {
        return R.layout.web_dialog;
    }

    @Override
    protected void initialize() {
        loadBar = findViewById(R.id.web_progress);
        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUserAgentString(getContext().getString(R.string.web_user_agent));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                loadBar.setProgress(newProgress, true);
                if (newProgress == 100) {
                    loadBar.animate().alpha(0f).setDuration(1000);
                } else if (newProgress < 100 && loadBar.getAlpha() != 1f) {
                    loadBar.setAlpha(1f);
                }
            }
        });
        webView.loadUrl(baseUrl);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
