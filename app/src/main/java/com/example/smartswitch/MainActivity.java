package com.example.smartswitch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.*;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    String ip = "192.168.0.23";

    private static long back_pressed;
    int alarmMode;
    int [] alarmHour = new int[2];
    int [] alarmMinute = new int[2];

    Button alarmResetButton0;
    Button alarmResetButton1;
    LinearLayout alarmLayout0;
    LinearLayout alarmLayout1;
    TextView alarmTime0;
    TextView alarmTime1;
    WebView webViewMain;
    EditText editTextIP;

    // 액티비티 생성 시
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmResetButton0 = findViewById(R.id.AlarmResetButton0);
        alarmResetButton1 = findViewById(R.id.AlarmResetButton1);
        alarmLayout0 = findViewById(R.id.AlarmLayout0);
        alarmLayout1 = findViewById(R.id.AlarmLayout1);
        alarmTime0 = findViewById(R.id.AlarmTime0);
        alarmTime1 = findViewById(R.id.AlarmTime1);
        webViewMain = findViewById(R.id.WebViewMain);
        editTextIP = findViewById(R.id.IP_Address);
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
    public void onClickAlarmResetButton0 (View view) {
        webViewMain.loadUrl(ip + "/setAlarm" + alarmMode + "?timeCode0=noAlarm");
        alarmTime0.setText("No Alarm");
    }

    // 알람 스위치 1 눌렀을 때
    public void onClickAlarmResetButton1 (View view) {
        webViewMain.loadUrl(ip + "/setAlarm" + alarmMode + "?timeCode1=noAlarm");
        alarmTime1.setText("No Alarm");
    }

    // 알람 설정 버튼 0 눌렀을 때
    public void onClickAlarmButton0(View view) {
        alarmMode = 0;
        showTimePickerDialog(alarmTime0);
    }

    // 알람 설정 버튼 1 눌렀을 때
    public void onClickAlarmButton1(View view) {
        alarmMode = 1;
        showTimePickerDialog(alarmTime1);
    }

    // ON 버튼을 눌렀을 때
    public void onClickButtonOn(View view) {
        ip = String.valueOf(editTextIP.getText());
        Toast.makeText(getApplicationContext(), "ButtonOn is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl(ip + "/onoff?LEDstatus=0");
    }


    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        ip = String.valueOf(editTextIP.getText());
        Toast.makeText(getApplicationContext(), "ButtonOff is clicked.", Toast.LENGTH_SHORT).show();
        webViewMain.loadUrl(ip + "/onoff?LEDstatus=1");
    }

    // 연결 버튼을 눌렀을 때
    public void onClickButtonConnect(View view) {
        Intent intent = new Intent(getApplicationContext(), WifiManagerActivity.class);
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
        ip = String.valueOf(editTextIP.getText());
        TimePickerDialog timePickerDialog = new TimePickerDialog
                (MainActivity.this, android.app.AlertDialog.THEME_HOLO_DARK, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmHour[alarmMode] = hourOfDay;
                        alarmMinute[alarmMode] = minute;
                        tvAlarmTime.setText(String.format("%02d : %02d", alarmHour[alarmMode], alarmMinute[alarmMode]));
                        webViewMain.loadUrl(ip + "/setAlarm" + alarmMode + "?timeCode" + alarmMode + "="
                                            + (alarmHour[alarmMode] * 100 + alarmMinute[alarmMode]));
                    }
                }, alarmHour[alarmMode], alarmMinute[alarmMode], true);
        timePickerDialog.show();
    }

    // 현재 접속 중인 WiFi의 IP 주소 리턴
    public String getIpAddressOfWifi(Context context) {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        int ipAddress = connectionInfo.getIpAddress();
        String ipString = String.format("%d.%d.%d.%d",
                (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        return ipString;
    }

}


