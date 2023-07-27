package com.skypan.covid_19;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class InformationActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSRL;
    private WebView mWvMain;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.item_home:
                intent = new Intent(this,MainActivity.class);
                break;
            case R.id.item_information:
                intent = new Intent(this,InformationActivity.class);
                break;
            case R.id.item_history:
                intent = new Intent(this,HistoryActivity.class);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        startActivity(intent);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mWvMain = findViewById(R.id.vw);
        mWvMain.getSettings().setJavaScriptEnabled(true);
        mWvMain.getSettings().setBuiltInZoomControls(true);
        mWvMain.getSettings().setDisplayZoomControls(false);
        mWvMain.setWebViewClient(new MyWebViewClient());
        mWvMain.setWebChromeClient(new MyWebChromeClient());
        mWvMain.loadUrl("https://www.worldometers.info/coronavirus/country/malaysia/");
        mSRL = findViewById(R.id.rl);
        mSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWvMain.reload();
                mSRL.setRefreshing(false);
            }
        });
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //mWvMain.loadUrl("javascript:alert('hello')");
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && mWvMain.canGoBack()){
            mWvMain.goBack();
            return true;        }
        return super.onKeyDown(keyCode, event);
    }
}