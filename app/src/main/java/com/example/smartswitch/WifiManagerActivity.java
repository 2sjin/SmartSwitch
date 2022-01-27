package com.example.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WifiManagerActivity extends AppCompatActivity {
    WebView wmWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_manager);

        wmWebView = findViewById(R.id.WebViewMain);

        wmWebView.setWebViewClient(new WebViewClient());    // 새창 없이 웹뷰 내에서 다시 열기
        wmWebView.getSettings().setJavaScriptEnabled(true); // 자바스크립트 허용
        wmWebView.loadUrl("192.168.4.1");
    }

    public void onclickButtonBack(View view) {
        if(wmWebView.canGoBack())
            wmWebView.goBack();     // 웹 페이지 뒤로 가기
        else
            Toast.makeText(getBaseContext(), "처음 페이지 입니다.", Toast.LENGTH_SHORT).show();
    }

    public void onclickButtonExit(View view) {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if(wmWebView.canGoBack())
            wmWebView.goBack();     // 웹 페이지 뒤로 가기
        else
            super.onBackPressed();  // 웹 종료
    }
    
}