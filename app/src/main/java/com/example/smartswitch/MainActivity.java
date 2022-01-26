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

    Switch alarmSwitch0;
    Switch alarmSwitch1;
    LinearLayout alarmLayout0;
    LinearLayout alarmLayout1;
    TextView alarmTime0;
    TextView alarmTime1;
    WebView webViewMain;

    // 액티비티 생성 시
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmSwitch0 = findViewById(R.id.AlarmSwitch0);
        alarmSwitch1 = findViewById(R.id.AlarmSwitch1);
        alarmLayout0 = findViewById(R.id.AlarmLayout0);
        alarmLayout1 = findViewById(R.id.AlarmLayout1);
        alarmTime0 = findViewById(R.id.AlarmTime0);
        alarmTime1 = findViewById(R.id.AlarmTime1);
        webViewMain = findViewById(R.id.WebViewMain);
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

    // 알람 스위치 0 눌렀을 때
    public void onClickAlarmSwitch1 (View view) {
        if(alarmSwitch0.isChecked() == true)
            alarmLayout0.setVisibility(View.VISIBLE);
        else
            alarmLayout0.setVisibility(View.INVISIBLE);
    }

    // 알람 스위치 1 눌렀을 때
    public void onClickAlarmSwitch2 (View view) {
        if(alarmSwitch1.isChecked() == true)
            alarmLayout1.setVisibility(View.VISIBLE);
        else
            alarmLayout1.setVisibility(View.INVISIBLE);
    }

    // 알람 설정 버튼 0 눌렀을 때
    public void onClickAlarmButton1(View view) {
        alarmMode = 0;
        showTimePickerDialog(alarmTime0);
    }

    // 알람 설정 버튼 1 눌렀을 때
    public void onClickAlarmButton2(View view) {
        alarmMode = 1;
        showTimePickerDialog(alarmTime1);
    }

    // ON 버튼을 눌렀을 때
    public void onClickButtonOn(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOn is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl("192.168.0.23/onoff?LEDstatus=0");
    }


    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOff is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl("192.168.0.23/onoff?LEDstatus=1");
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
                        webViewMain.loadUrl("192.168.0.23/setAlarm" + alarmMode + "?timeCode" + alarmMode + "="
                                            + (alarmHour[alarmMode] * 100 + alarmMinute[alarmMode]));
                    }
                }, alarmHour[alarmMode], alarmMinute[alarmMode], true);
        timePickerDialog.show();
    }

}


