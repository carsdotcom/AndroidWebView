package com.example.javawebview;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView myWebView = (WebView) findViewById(R.id.webview);

        String address = getString(R.string.address);
        String protocol = getString(R.string.protocol);
        String url = protocol + address;

        myWebView.loadUrl(url);
    }
}
