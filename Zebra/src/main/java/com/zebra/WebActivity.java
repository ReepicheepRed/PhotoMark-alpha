package com.zebra;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.zebra.base.BaseActivity;
import com.zebra.login.LoginUserUtils;
import com.zebra.util.Constant;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by zhiPeng.S on 2016/6/20.
 */
@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity {
    private SharedPreferences preferences;

    enum Type{
        DETAIL_PHOTO,DETAIL_UNVEIL,DETAIL_BASK,DETAIL_CALCULATE
    }


    private String content_url = "";

    @ViewInject(R.id.actionbar_title_sns)
    private TextView title_web;

    @ViewInject(R.id.content_wv)
    private WebView content_web;

    @Event({R.id.actionbar_back_iv_sns,R.id.actionbar_right_sns})
    private void viewClick(View view){
        switch(view.getId()){
            case R.id.actionbar_back_iv_sns:
                this.finish();
                break;
            case R.id.actionbar_right_sns:
                content_web.loadUrl(content_url);
                break;
        }
    }

    @Override
    protected void initView() {
        initWebView();
    }

    @Override
    protected void initData() {
        super.initData();
        preferences = LoginUserUtils.getUserSharedPreferences(this);

        Intent intent = getIntent();
        int content_flag = intent.getIntExtra(Constant.WEB_H5,-1);
        switch (content_flag){
            case Constant.BANNER:
                content_url = intent.getStringExtra("link");
                break;
            default:
                content_url = "http://www.baidu.com";
                break;
        }

        content_web.loadUrl(content_url);
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView(){
        WebSettings webSettings = content_web.getSettings();
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);

        WebChromeClient wvcc = new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                Log.d("ANDROID_LAB", "TITLE=" + title);
                title_web.setText(title);
            }

        };
        content_web.setWebChromeClient(wvcc);

        WebViewClient wvc = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                content_web.loadUrl(url);
                return true;
            }
        };
        content_web.setWebViewClient(wvc);

//        content_web.addJavascriptInterface(new JsOperation(this), "client");
    }

}
