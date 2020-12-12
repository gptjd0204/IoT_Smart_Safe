package com.example.smartsafe_restapi.ui.apicall;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartsafe_restapi.R;
import com.example.smartsafe_restapi.httpconnection.GetRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetOpenLog extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetOpenLog(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    // 금고 여닫힘 시간 로그 조회
    @Override
    protected void onPreExecute() {
        try {
            TextView textView_Open_Date1 = activity.findViewById(R.id.textView_open_date1);
            TextView textView_Open_Date2 = activity.findViewById(R.id.textView_open_date2);
            // 사용자가 설정한 날짜에 발생한 금고 여닫힘 로그 조회
            String params = String.format("?from=%s:00&to=%s:00",textView_Open_Date1.getText().toString(),
                    textView_Open_Date2.getText().toString());

            Log.i(TAG,"urlStr="+urlStr+params);
            url = new URL(urlStr+params);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        TextView message = activity.findViewById(R.id.open_message2);
        message.setText("조회중...");
    }

    @Override
    protected void onPostExecute(String jsonString) {
        TextView message = activity.findViewById(R.id.open_message2);
        if (jsonString == null) {
            message.setText("로그 없음");
            return;
        }
        message.setText("");
        ArrayList<GetOpenLog.Tag> arrayList = getArrayListFromJSONString(jsonString);

        // DynamoDB에 OpenLogging에 있는 데이터를 가져와 표시
        final ArrayAdapter adapter = new ArrayAdapter(activity,
                android.R.layout.simple_list_item_1,
                arrayList.toArray());
        ListView txtList = activity.findViewById(R.id.open_logList);
        txtList.setAdapter(adapter);
        txtList.setDividerHeight(10);
    }

    // DynamoDB에 OpenLogging에 있는 데이터를 RestAPI를 이용하여 가져옴
    protected ArrayList<GetOpenLog.Tag> getArrayListFromJSONString(String jsonString) {
        ArrayList<GetOpenLog.Tag> output = new ArrayList();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");

            Log.i(TAG, "jsonString="+jsonString);

            JSONObject root = new JSONObject(jsonString);
            JSONArray jsonArray = root.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = (JSONObject)jsonArray.get(i);

                GetOpenLog.Tag thing = new GetOpenLog.Tag(jsonObject.getString("SAFE"),
                        jsonObject.getString("timestamp"));

                output.add(thing);
            }

        } catch (JSONException e) {
            //Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }

    // Tag 수정
    class Tag {
        String SAFE;
        String timestamp;

        public Tag(String safe, String time) {
            SAFE = safe;
            timestamp = time;
        }

        public String toString() {
            String str;
            if(SAFE.equals("OPEN")){
                str = String.format("금고가 열렸습니다! [%s]", timestamp);
            } else {
                str = String.format("금고가 닫혔습니다! [%s]", timestamp);
            }
            return str;
        }
    }
}

