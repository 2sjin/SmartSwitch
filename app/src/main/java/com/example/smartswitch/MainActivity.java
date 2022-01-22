package com.example.smartswitch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    static int portNumber = 80;

    Switch alarmSwitch;
    LinearLayout alarmLayout;
    EditText etPortNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadView();
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
    }

    // OFF 버튼을 눌렀을 때
    public void onClickButtonOff(View view) {
        Toast.makeText(getApplicationContext(), "ButtonOff is clicked.", Toast.LENGTH_SHORT).show();
    }

    // View 불러오기
    public void loadView() {
        alarmSwitch = findViewById(R.id.alarmSwitch);
        alarmLayout = findViewById(R.id.alarmLayout);
        etPortNumber = findViewById(R.id.etPortNumber);
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
}