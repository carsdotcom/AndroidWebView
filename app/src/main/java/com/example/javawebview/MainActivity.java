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

        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        String address = getString(R.string.address);
        String protocol = getString(R.string.protocol);
        String port = getString(R.string.port);

        String url = protocol + address + port + "/cars/dealer-locator";
        //String url = protocol + address + port + "/detail/new-2025-subaru-outback-wilderness-all-wheel-drive-sport-utility-4s4btgud1s3163789";

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

            String jsCode = "console.log(typeof window.NativeToThirdPartyWVChaseJSBridgeHandler.shouldNativeHandleButtonPressed);";
            myWebView.evaluateJavascript(jsCode, null);
            jsCode = "window.NativeToThirdPartyWVChaseJSBridgeHandler.shouldNativeHandleButtonPressed('BACK');";
            myWebView.evaluateJavascript(jsCode, null);
            jsCode = "console.log('called to shouldNativeHandleButtonPressed(BACK)');";
            myWebView.evaluateJavascript(jsCode, null);

            return true;
        } else if (id == R.id.nav_font_scale_inc) {
            WebView myWebView = findViewById(R.id.webview);

            // call web function, increase font scale factor
            String jsCode = "window?.JPKHybridSyncKit?.set({'command':'updateDynamicFont',options: {'fontScaleFactor':1.1}});";
            myWebView.evaluateJavascript(jsCode, null);
        } else if (id == R.id.nav_font_scale_dec) {
            WebView myWebView = findViewById(R.id.webview);

            // call web function, decrease font scale factor
            String jsCode = "window?.JPKHybridSyncKit?.set({'command':'updateDynamicFont',options: {'fontScaleFactor':0.9}});";
            myWebView.evaluateJavascript(jsCode, null);
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
        public void shouldNativeHandleButtonPressed(String command) {
            Toast.makeText(mContext, "shouldNativeHandleButtonPressed: " + command, Toast.LENGTH_LONG).show();
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
