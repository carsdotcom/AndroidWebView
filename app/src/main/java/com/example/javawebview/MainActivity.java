package com.example.javawebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        WebView myWebView = findViewById(R.id.webview);

        Toolbar toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyWebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String jsCode = "window.ThirdPartyWVToNativeChaseJSBridgeHandler = {};";
                myWebView.evaluateJavascript(jsCode, null);
                jsCode = "window.ThirdPartyWVToNativeChaseJSBridgeHandler.externalBrowser = function(data) { console.log('Android app handling externalBrowser call!');console.log('url', data.url);console.log('speedbump', data.speedBump); };";
                myWebView.evaluateJavascript(jsCode, null);
            }
        });

        String address = getString(R.string.address);
        String protocol = getString(R.string.protocol);
        String port = getString(R.string.port);
        //String url = protocol + address + port + "/dealer-locator";
        String url = protocol + address + port + "/detail/new-2025-subaru-outback-wilderness-all-wheel-drive-sport-utility-4s4btgud1s3163789";

        myWebView.loadUrl(url);
    }

    // @link https://web.archive.org/web/20230203152426/http://tools.android.com/tips/non-constant-fields
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        drawer.closeDrawer(GravityCompat.START);

        if (id == R.id.nav_back) {
            Toast.makeText(this, "Back button pressed", Toast.LENGTH_SHORT).show();

            // Run JS on the site to signal the mobile back button was pressed
            WebView myWebView = findViewById(R.id.webview);
            myWebView.evaluateJavascript("javascript:exampleWebsiteFunction();", null);

            return true;
        }

        return true;
    }

    // @link https://developer.android.com/develop/ui/views/layout/webapps/webview#UsingJavaScript
    public static class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void shouldNativeHandleButtonPress(String command) {
            Toast.makeText(mContext, "shouldNativeHandleButtonPress: " + command, Toast.LENGTH_LONG).show();
        }
    }

    // @link https://developer.android.com/develop/ui/views/layout/webapps/webview#HandlingNavigation
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String host = request.getUrl().getHost();
            if (host == null) {
                Toast.makeText(MainActivity.this, "Host is null", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (getString(R.string.address).equals(host)) {
                Toast.makeText(MainActivity.this, "Same domain - no new intent redirect", Toast.LENGTH_SHORT).show();
                return false;
            }

            Toast.makeText(MainActivity.this, "Different domain - new intent redirect", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
    }
}
