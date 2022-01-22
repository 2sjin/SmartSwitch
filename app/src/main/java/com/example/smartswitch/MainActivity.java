package com.example.smartswitch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

public class MainActivity extends AppCompatActivity {
    static int portNumber = 80;
    private static long back_pressed;

    Switch alarmSwitch;
    LinearLayout alarmLayout;
    EditText etPortNumber;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadView();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // 뒤로가기 버튼을 눌렀을 때
    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
            finish();
        }
        else {
            Toast.makeText(getBaseContext(), "한번 더 뒤로가기를 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }

    // 알람 스위치를 눌렀을 때
    public void onClickAlarmSwitch (View view) {
        if(alarmSwitch.isChecked() == true)
            alarmLayout.setVisibility(View.VISIBLE);
        else
            alarmLayout.setVisibility(View.GONE);
    }

    // ON 버튼을 눌렀을 때
    public void onClickButtonOn(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOn is clicked.", Toast.LENGTH_SHORT).show();
        webView.loadUrl("192.168.0.23/ledOn");
    }

    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOff is clicked.", Toast.LENGTH_SHORT).show();
        webView.loadUrl("192.168.0.23/ledOff");
    }

    // View 불러오기
    public void loadView() {
        alarmSwitch = findViewById(R.id.alarmSwitch);
        alarmLayout = findViewById(R.id.alarmLayout);
        etPortNumber = findViewById(R.id.etPortNumber);
        webView = findViewById(R.id.webView);
    }

    // 테스트
    public void startTest(View view) {
        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
        if(!String.valueOf(etPortNumber.getText()).equals(""))
            portNumber = Integer.parseInt(String.valueOf(etPortNumber.getText()));
        else
            portNumber = 80;
        startActivity(intent);
    }

    // 다이얼로그 출력
    public void showErrorDialog(String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}


