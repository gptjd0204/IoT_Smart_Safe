package com.example.smartsafe_restapi.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsafe_restapi.R;
import com.example.smartsafe_restapi.ui.apicall.GetThingShadow;
import com.example.smartsafe_restapi.ui.apicall.UpdateShadow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class DeviceActivity extends AppCompatActivity {
    String urlStr;
    final static String TAG = "AndroidAPITest";
    Timer timer;
    Button startGetBtn;
    Button stopGetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_safe);
        Intent intent = getIntent();
        urlStr = intent.getStringExtra("thingShadowURL");

        startGetBtn = findViewById(R.id.startGetBtn);
        startGetBtn.setEnabled(true);
        startGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                                   @Override
                                   public void run() {
                                       new GetThingShadow(DeviceActivity.this, urlStr).execute();
                                   }
                               },
                        0,2000);

                startGetBtn.setEnabled(false);
                stopGetBtn.setEnabled(true);
            }
        });

        stopGetBtn = findViewById(R.id.stopGetBtn);
        stopGetBtn.setEnabled(false);
        stopGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (timer != null)
                    timer.cancel();
                clearTextView();
                startGetBtn.setEnabled(true);
                stopGetBtn.setEnabled(false);
            }
        });

        // 앱에서 충격 감지 실행 (아두이노 디바이스 제어)
        Button updateRunBtn = findViewById(R.id.updateRunBtn);
        updateRunBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit_shockrun = "RUN";

                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String shockrun_input = edit_shockrun;
                    if (shockrun_input != null && !shockrun_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "SHOCK_RUN");
                        tag1.put("tagValue", shockrun_input);

                        jsonArray.put(tag1);
                    }

                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                    Toast.makeText(DeviceActivity.this,"충격 감지를 실행합니다", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });

        // 앱에서 충격 감지 중지 (아두이노 디바이스 제어)
        Button updateStopBtn = findViewById(R.id.updateStopBtn);
        updateStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String edit_shockrun = "STOP";

                JSONObject payload = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray();
                    String shockrun_input = edit_shockrun;
                    if (shockrun_input != null && !shockrun_input.equals("")) {
                        JSONObject tag1 = new JSONObject();
                        tag1.put("tagName", "SHOCK_RUN");
                        tag1.put("tagValue", shockrun_input);

                        jsonArray.put(tag1);
                    }

                    if (jsonArray.length() > 0)
                        payload.put("tags", jsonArray);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONEXception");
                }
                Log.i(TAG,"payload="+payload);
                if (payload.length() >0 ) {
                    new UpdateShadow(DeviceActivity.this, urlStr).execute(payload);
                    Toast.makeText(getApplicationContext(),"충격 감지를 중지합니다", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(DeviceActivity.this,"변경할 상태 정보 입력이 필요합니다", Toast.LENGTH_SHORT).show();
            }
        });


    }



    private void clearTextView() {
        TextView reported_shockrunTV = findViewById(R.id.reported_shockrun);
        reported_shockrunTV.setText("");
    }

}