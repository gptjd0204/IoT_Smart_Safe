package com.example.smartsafe_restapi.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smartsafe_restapi.R;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "AndroidAPITest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 충격 감지 상태 조회 및 변경
        Button thingShadowBtn = findViewById(R.id.thingShadowBtn);
        thingShadowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlstr = "https://uolgogc4l9.execute-api.ap-northeast-2.amazonaws.com/prod/devices/SmartSAFE";
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "충격 감지 상태 조회/변경 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                intent.putExtra("thingShadowURL", urlstr);
                startActivity(intent);

            }
        });


        // 금고 여닫힘 시간 조회
        Button openListLogsBtn = findViewById(R.id.openLogsBtn);
        openListLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlstr = "https://uolgogc4l9.execute-api.ap-northeast-2.amazonaws.com/prod/devices/SmartSAFE/openlog";
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "금고 여닫힘 시간 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, OpenLogActivity.class);
                intent.putExtra("getOpenLogsURL", urlstr);
                startActivity(intent);
            }
        });


        // 충격 발생 시간 조회
        Button shockListLogsBtn = findViewById(R.id.shockLogsBtn);
        shockListLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlstr = "https://uolgogc4l9.execute-api.ap-northeast-2.amazonaws.com/prod/devices/SmartSAFE/shocklog";
                if (urlstr == null || urlstr.equals("")) {
                    Toast.makeText(MainActivity.this, "충격 발생 시간 조회 API URI 입력이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ShockLogActivity.class);
                intent.putExtra("getShockLogsURL", urlstr);
                startActivity(intent);
            }
        });

    }
}