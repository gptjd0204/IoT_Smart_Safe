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
import com.example.smartsafe_restapi.ui.apicall.GetOpenLog;


import java.util.Calendar;


public class OpenLogActivity extends AppCompatActivity {
    String getOpenLogsURL;

    private TextView textView_Date1;
    private TextView textView_Date2;
    private DatePickerDialog.OnDateSetListener callbackMethod;
    final static String TAG = "AndroidAPITest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_log);

        Intent intent = getIntent();
        getOpenLogsURL = intent.getStringExtra("getOpenLogsURL");
        Log.i(TAG, "getOpenLogsURL="+getOpenLogsURL);

        // 일별 금고 여닫힘 시간 로그 조회
        Button openDayBtn = findViewById(R.id.open_day_button);
        openDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackMethod = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {

                        // 사용자가 입력한 조회 시작 시간과 조회 종료 시간 표시
                        textView_Date1 = (TextView)findViewById(R.id.textView_open_date1);
                        textView_Date2 = (TextView)findViewById(R.id.textView_open_date2);
                        textView_Date1.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)+"00:00:00");
                        textView_Date2.setText(String.format("%d-%d-%d ", year ,monthOfYear+1,dayOfMonth)+ "23:59:59");
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(OpenLogActivity.this, callbackMethod, 2020, 12, 0);

                dialog.show();
            }
        });

        // 월별 금고 여닫힘 시간 로그 조회
        Button openMonthBtn = findViewById(R.id.open_month_button);
        openMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
                        Calendar calendar= Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear-1);

                        // 사용자가 입력한 조회 시작 시간과 조회 종료 시간 표시
                        textView_Date1 = (TextView)findViewById(R.id.textView_open_date1);
                        textView_Date2 = (TextView)findViewById(R.id.textView_open_date2);
                        textView_Date1.setText(String.format("%d-%d-%d ", year,monthOfYear, dayOfMonth)+"00:00:00");
                        textView_Date2.setText(String.format("%d-%d-%d ", year,monthOfYear, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))+ "23:59:59");
                    }
                };

                MyYearMonthPickerDialog pd = new MyYearMonthPickerDialog();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(), "YearMonthPickerTest");
            }
        });

        // 금고 여닫힘 시간 로그 조회 시작
        Button start = findViewById(R.id.open_log_start_button);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetOpenLog(OpenLogActivity.this,getOpenLogsURL).execute();
            }
        });
    }
}