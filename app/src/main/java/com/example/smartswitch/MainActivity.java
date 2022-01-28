package com.example.smartswitch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    String [] STR_MACRO = {"Unknown Data", "Alarm Off"};
    static String ip = "192.168.0.23";

    static SharedPreferences pref;
    static SharedPreferences.Editor editor;

    private static long back_pressed;
    int alarmMode;
    int [] alarmHour = new int[2];
    int [] alarmMinute = new int[2];

    Button actionButton0, actionButton1, buttonWiFi;
    Button alarmButton0, alarmButton1, alarmResetButton0, alarmResetButton1;
    LinearLayout alarmLayout0, alarmLayout1;
    TextView tvAlarm0, tvAlarm1, alarmTime0, alarmTime1;
    WebView webViewMain;

    // 액티비티 생성 시
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences("SMART_SWITCH", Context.MODE_PRIVATE);
        editor = pref.edit();

        actionButton0 = findViewById(R.id.actionButton0);
        actionButton1 = findViewById(R.id.actionButton1);
        buttonWiFi = findViewById(R.id.buttonWiFi);
        alarmButton0 = findViewById(R.id.AlarmButton0);
        alarmButton1 = findViewById(R.id.AlarmButton1);
        alarmResetButton0 = findViewById(R.id.AlarmResetButton0);
        alarmResetButton1 = findViewById(R.id.AlarmResetButton1);
        alarmLayout0 = findViewById(R.id.AlarmLayout0);
        alarmLayout1 = findViewById(R.id.AlarmLayout1);
        tvAlarm0 = findViewById(R.id.tvAlarm0);
        tvAlarm1 = findViewById(R.id.tvAlarm1);
        alarmTime0 = findViewById(R.id.AlarmTime0);
        alarmTime1 = findViewById(R.id.AlarmTime1);
        webViewMain = findViewById(R.id.WebViewMain);

        actionButton0.setText(pref.getString("name_act0", "ON"));
        actionButton1.setText(pref.getString("name_act1", "OFF"));
        tvAlarm0.setText("◆ 알람: " + pref.getString("name_act0", "ON"));
        tvAlarm1.setText("◆ 알람: " + pref.getString("name_act1", "OFF"));
        alarmTime0.setText(pref.getString("alarm0", STR_MACRO[0]));
        alarmTime1.setText(pref.getString("alarm1", STR_MACRO[0]));

        String getAddress = MainActivity.pref.getString("server_ip", "192.168.0.23");
        ip = getAddress;

        // 버튼 0 길게 눌렀을 때
        actionButton0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEditNameDialog(0, actionButton0, tvAlarm0);
                return true;
            }
        });

        // 버튼 1 길게 눌렀을 때
        actionButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showEditNameDialog(1, actionButton1, tvAlarm1);
                return true;
            }
        });

        // Wi-Fi 설정 버튼 길게 눌렀을 때
        buttonWiFi.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showWiFiResetDialog();
                return true;
            }
        });

        // 웹뷰 클라이언트 연결 오류 시
        webViewMain.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                alarmTime0.setText(STR_MACRO[0]);
                alarmTime1.setText(STR_MACRO[0]);
                editor.putString("alarm0", STR_MACRO[0]);
                editor.putString("alarm1", STR_MACRO[0]);
                editor.commit();
                showConnectionErrorDialog();
            }
        });

    }

    // 액티비티 재개 시
    @Override
    protected void onResume() {
        super.onResume();
        webViewMain.loadUrl(ip);
    }

    // 액티비티 종료 시
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finishAndRemoveTask();
        Process.killProcess(Process.myPid());
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

    // 알람 끄기 버튼 0 눌렀을 때
    public void onClickAlarmResetButton0 (View view) {
        webViewMain.loadUrl(ip + "/setAlarm0?timeCode0=noAlarm");
        alarmTime0.setText(STR_MACRO[1]);
        editor.putString("alarm0", STR_MACRO[1]);
        editor.commit();
    }

    // 알람 끄기 버튼 1 눌렀을 때
    public void onClickAlarmResetButton1 (View view) {
        webViewMain.loadUrl(ip + "/setAlarm1?timeCode1=noAlarm");
        alarmTime1.setText(STR_MACRO[1]);
        editor.putString("alarm1", STR_MACRO[1]);
        editor.commit();
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
        webViewMain.loadUrl(ip + "/onoff?LEDstatus=0");
    }

    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        webViewMain.loadUrl(ip + "/onoff?LEDstatus=1");
    }

    // Wi-Fi 연결 버튼을 눌렀을 때
    public void onClickButtonConnect(View view) {
        Intent intent = new Intent(getApplicationContext(), WifiManagerActivity.class);
        startActivity(intent);
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

    // WiFi 재설정 다이얼로그 출력
    public void showWiFiResetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wi-Fi 연결 해제");
        builder.setMessage("현재 접속 중인 Wi-Fi 네트워크와의 연결을 해제할까요?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webViewMain.loadUrl(ip + "/wifi");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("NO",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    // 접속 오류 다이얼로그 출력
    public void showConnectionErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("서버 접속 실패");
        builder.setMessage("WIFI 설정 화면에서 다음을 확인해주세요.\n"
                            + "1. 단말기 WiFi \"SmartSwitch\"에 연결 후,\n[Configure WiFi]를 진행하였나요?\n"
                            + "2. 서버 주소(Server Address)가 올바르게 입력되었나요?\n"
                            + "3. 그 외 장치의 전원 및 네트워크 설정에는 문제가 없나요?");
        builder.setCancelable(false);
        builder.setPositiveButton("WIFI 설정",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), WifiManagerActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("재접속",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                webViewMain.loadUrl(ip);
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("앱 종료",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onDestroy();
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
                        String tempStr = String.format("%02d : %02d", alarmHour[alarmMode], alarmMinute[alarmMode]);
                        tvAlarmTime.setText(tempStr);
                        editor.putString("alarm" + alarmMode, tempStr);
                        editor.commit();
                        webViewMain.loadUrl(ip + "/setAlarm" + alarmMode + "?timeCode" + alarmMode + "="
                                            + (alarmHour[alarmMode] * 100 + alarmMinute[alarmMode]));
                    }
                }, alarmHour[alarmMode], alarmMinute[alarmMode], true);
        timePickerDialog.show();
    }

    // 버튼 및 알람 이름 변경을 위한 입력 다이얼로그 출력
    public void showEditNameDialog(int flag, Button btn, TextView tv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText et = new EditText(this);
        et.setText(btn.getText());
        builder.setView(et);
        builder.setTitle("변경할 텍스트 입력");
        builder.setCancelable(true);
        builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                btn.setText(et.getText());
                tv.setText("◆ 알람: " + et.getText());
                editor.putString("name_act" + flag, String.valueOf(et.getText()));
                editor.commit();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}


