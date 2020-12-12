package com.example.smartsafe_restapi.ui.apicall;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartsafe_restapi.R;
import com.example.smartsafe_restapi.httpconnection.GetRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class GetThingShadow extends GetRequest {
    final static String TAG = "AndroidAPITest";
    String urlStr;
    public GetThingShadow(Activity activity, String urlStr) {
        super(activity);
        this.urlStr = urlStr;
    }

    // 충격 감지 상태 조회

    @Override
    protected void onPreExecute() {
        try {
            Log.e(TAG, urlStr);
            url = new URL(urlStr);

        } catch (MalformedURLException e) {
            Toast.makeText(activity,"URL is invalid:"+urlStr, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            activity.finish();
        }
    }

    @Override
    protected void onPostExecute(String jsonString) {
        if (jsonString == null)
            return;
        Map<String, String> state = getStateFromJSONString(jsonString);
        TextView reported_shockrunTV = activity.findViewById(R.id.reported_shockrun);

        // 아두이노 디바이스에 상태를 조회하여 충격 감지 상태 조회
        if (state.get("reported_SHOCK_RUN").equals("RUN")){
            reported_shockrunTV.setText("충격 감지 실행중");
            reported_shockrunTV.setTextColor(Color.parseColor("#17c217"));
        } else {
            reported_shockrunTV.setText("충격 감지 중지");
            reported_shockrunTV.setTextColor(Color.parseColor("#ff0000"));
        }
    }

    protected Map<String, String> getStateFromJSONString(String jsonString) {
        Map<String, String> output = new HashMap<>();
        try {
            // 처음 double-quote와 마지막 double-quote 제거
            jsonString = jsonString.substring(1,jsonString.length()-1);
            // \\\" 를 \"로 치환
            jsonString = jsonString.replace("\\\"","\"");
            Log.i(TAG, "jsonString="+jsonString);
            JSONObject root = new JSONObject(jsonString);
            JSONObject state = root.getJSONObject("state");
            JSONObject reported = state.getJSONObject("reported");
            String shockrunValue = reported.getString("SHOCK_RUN");
            output.put("reported_SHOCK_RUN", shockrunValue);

            JSONObject desired = state.getJSONObject("desired");
            String desired_shockrunValue = desired.getString("SHOCK_RUN");
            output.put("desired_SHOCK_RUN", desired_shockrunValue);


        } catch (JSONException e) {
            Log.e(TAG, "Exception in processing JSONString.", e);
            e.printStackTrace();
        }
        return output;
    }
}