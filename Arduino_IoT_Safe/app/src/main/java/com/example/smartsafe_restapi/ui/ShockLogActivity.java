package com.example.smartsafe_restapi.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.smartsafe_restapi.R;

import com.example.smartsafe_restapi.ui.apicall.GetShockLog;

import java.util.Calendar;

public class ShockLogActivity extends AppCompatActivity {
    String getShockLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shock_log);

        Intent intent = getIntent();
        getShockLogsURL = intent.getStringExtra("getShockLogsURL");
        Log.i(TAG, "getShockLogsURL="+getShockLogsURL);

        // 일별 충격 발생 시간 로그 조회
        Button shockDayBtn = findViewById(R.id.shock_day_button);
        shockDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {

                        // 사용자가 입력한 조회 시작 시간과 조회 종료 시간 표시
                        textView_Date1 = (TextView)findViewById(R.id.textView_shock_date1);
                        textView_Date2 = (TextView)findViewById(R.id.textView_shock_date2);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)+"00:00:00");
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)+ "23:59:59");
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(ShockLogActivity.this, callbackMethod, 2020, 12, 0);

                dialog.show();
            }
        });

        // 월별 충격 발생 시간 로그 조회
        Button shockMonthBtn = findViewById(R.id.shock_month_button);
        shockMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        Calendar calendar= Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear-1);

                        // 사용자가 입력한 조회 시작 시간과 조회 종료 시간 표시
                        textView_Date1 = (TextView)findViewById(R.id.textView_shock_date1);
                        textView_Date2 = (TextView)findViewById(R.id.textView_shock_date2);
                        textView_Date1.setText(String.format("%d-%d-%d ", year,monthOfYear, dayOfMonth)+"00:00:00");
                        textView_Date2.setText(String.format("%d-%d-%d ", year,monthOfYear, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))+ "23:59:59");
                    }
                };

                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });


        // 충격 발생 시간 로그 조회 시작
        Button start = findViewById(R.id.shock_log_start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetShockLog(ShockLogActivity.this,getShockLogsURL).execute();
            }
        });
    }
}

