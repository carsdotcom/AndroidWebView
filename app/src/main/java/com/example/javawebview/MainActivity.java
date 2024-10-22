package com.example.javawebview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = findViewById(R.id.webview);

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        String address = getString(R.string.address);
        String protocol = getString(R.string.protocol);
        String port = getString(R.string.port);
        String url = protocol + address + port;

        myWebView.loadUrl(url);
    }

    // @link https://developer.android.com/develop/ui/views/layout/webapps/webview#UsingJavaScript
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }
    }

    // @link https://developer.android.com/develop/ui/views/layout/webapps/webview#HandlingNavigation
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (getString(R.string.address).equals(request.getUrl().getHost())) {
                // This is your website, so don't override. Let your WebView load the
                // page.
                return false;
            }

            // Otherwise, the link isn't for a page on your site, so launch another
            // Activity that handles URLs.
            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
    }
}
