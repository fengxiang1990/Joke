package com.app.fxa.joke.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.fxa.joke.BaseActivity;
import com.app.fxa.joke.R;


public class AboutWebActivity extends BaseActivity {


    WebView webView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle bundle) {
        // TODO Auto-generated method stub
        super.onCreate(bundle);
        this.setContentView(R.layout.activity_about);
        webView = (WebView) this.findViewById(R.id.webView);
        toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle("关于");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutWebActivity.this.finish();
            }
        });
        initData();
    }

    public void initData() {
        // TODO Auto-generated method stub
        // 支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(false);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(false);
        // 扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);

        // 自适应屏幕
        webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { // 重写此方法表明点击网页里面的链接还是在当前的web里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }

        });
        webView.loadUrl("file:///android_asset/page/about.html");
    }

}
