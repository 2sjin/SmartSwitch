package com.example.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WifiManagerActivity extends AppCompatActivity {
    WebView wmWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_manager);

        wmWebView = findViewById(R.id.wmWebView);

        wmWebView.setWebViewClient(new WebViewClient());    // 새창 없이 웹뷰 내에서 다시 열기
        wmWebView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 허용
        wmWebView.loadUrl("192.168.4.1");
    }
    
    @Override
    public void onBackPressed() {
        if(wmWebView.canGoBack())
            wmWebView.goBack();     // 웹 페이지 뒤로 가기
        else
            super.onBackPressed();  // 웹 종료
    }
    
}