package com.bangjeck.page;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

public class BangJeckBrowser extends BangJeckSetting {

    boolean ssl= false;
    boolean errorPolicy;
    ProgressBar progressBar;
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.bangjeckbrowser);

        progressBar = (ProgressBar)findViewById(R.id.progress);
        String url = base_url+"maintenance.html";

        try{
            url = getIntent().getExtras().getString("url");
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

        browser = (WebView)findViewById(R.id.browser);
        browser.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if(newProgress>=100&&!errorPolicy){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        browser.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Handle the error
                errorPolicy = true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!ssl&&errorPolicy){
                    Toast.makeText(getApplicationContext(),"Terjadi kesalahan koneksi.",Toast.LENGTH_SHORT).show();
                    browser.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        browser.clearCache(true);
        browser.clearHistory();
        browser.clearFormData();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(true);
        browser.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (browser.canGoBack()) {
                        browser.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}