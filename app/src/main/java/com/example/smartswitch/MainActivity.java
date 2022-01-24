package com.example.smartswitch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

public class MainActivity extends AppCompatActivity {
    private static long back_pressed;
    int alarmMode;
    int [] alarmHour = new int[2];
    int [] alarmMinute = new int[2];

    Switch alarmSwitch1;
    Switch alarmSwitch2;
    LinearLayout alarmLayout1;
    LinearLayout alarmLayout2;
    TextView alarmTime1;
    TextView alarmTime2;
    WebView webViewMain;
    WebView webViewForAlarm;

    // 액티비티 생성 시
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSwitch1 = findViewById(R.id.AlarmSwitch1);
        alarmSwitch2 = findViewById(R.id.AlarmSwitch2);
        alarmLayout1 = findViewById(R.id.AlarmLayout1);
        alarmLayout2 = findViewById(R.id.AlarmLayout2);
        alarmTime1 = findViewById(R.id.AlarmTime1);
        alarmTime2 = findViewById(R.id.AlarmTime2);
        webViewMain = findViewById(R.id.WebViewMain);
        webViewForAlarm = findViewById(R.id.WebViewForAlarm);

        webViewForAlarm.getSettings().setJavaScriptEnabled(true);
        webViewForAlarm.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    }

    // 액티비티 재개 시
    @Override
    protected void onResume() {
        super.onResume();
        webViewForAlarm.loadUrl("192.168.0.23/alarm");
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

    // 알람 스위치 1 눌렀을 때
    public void onClickAlarmSwitch1 (View view) {
        if(alarmSwitch1.isChecked() == true)
            alarmLayout1.setVisibility(View.VISIBLE);
        else
            alarmLayout1.setVisibility(View.INVISIBLE);
    }

    // 알람 스위치 2 눌렀을 때
    public void onClickAlarmSwitch2 (View view) {
        if(alarmSwitch2.isChecked() == true)
            alarmLayout2.setVisibility(View.VISIBLE);
        else
            alarmLayout2.setVisibility(View.INVISIBLE);
    }

    // 알람 설정 버튼 1 눌렀을 때
    public void onClickAlarmButton1(View view) {
        alarmMode = 0;
        showTimePickerDialog(alarmTime1);
    }

    // 알람 설정 버튼 2 눌렀을 때
    public void onClickAlarmButton2(View view) {
        alarmMode = 1;
        showTimePickerDialog(alarmTime2);
    }

    // ON 버튼을 눌렀을 때
    public void onClickButtonOn(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOn is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl("192.168.0.23/on");
    }


    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOff is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl("192.168.0.23/off");
    }

    // 연결 버튼을 눌렀을 때
    public void onClickButtonConnect(View view) {
        Intent intent = new Intent(getApplicationContext(), WifiManagerActivity.class);
        startActivity(intent);
    }

    // 테스트 버튼을 눌렀을 때
    public void startTest(View view) {
        Intent intent = new Intent(getApplicationContext(), TestActivity.class);
        startActivity(intent);
    }

    // 에러 다이얼로그 출력
    public void showErrorDialog(String message) {
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

    // TimePicker 다이얼로그 출력(알람 설정)
    public void showTimePickerDialog(TextView tvAlarmTime) {
        TimePickerDialog timePickerDialog = new TimePickerDialog
                (MainActivity.this, android.app.AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmHour[alarmMode] = hourOfDay;
                        alarmMinute[alarmMode] = minute;
                        tvAlarmTime.setText(String.format("%02d : %02d", alarmHour[alarmMode], alarmMinute[alarmMode]));
                        webViewForAlarm.loadUrl("javascript:setAlarm" + alarmMode
                                        + "(" + alarmHour[alarmMode] + ", " + alarmMinute[alarmMode] + ")");
                    }
                }, alarmHour[alarmMode], alarmMinute[alarmMode], true);
        timePickerDialog.show();
    }

}


