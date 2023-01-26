// used to setup the notification service.
package com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan;

import android.app.Activity;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sistempengurusanjabatanjuruteknik.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender  {
    String userFcmToken;
    String title;
    String body;
    Context mContext;
    Activity mActivity;

    private final String postUrl = "https://fcm.googleapis.com/fcm/send";
    private final String fcmServerKey ="AAAAG6GFdWY:APA91bFfeyZbi217IRPWy4-sXPhtfbhJjdAXUKTXeTgHAWcvQ8iyUsMI9AOdnL8uUMgQjUsbu5v9XGTKurqXPHEVMy-SoJBgfEsmLrGEHQqZmT_4cLenCOhfi-Rkw0MJXV2Yx_BhvxzH";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext, Activity mActivity) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    public void SendNotifications() {
        RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        JSONObject mainObj = new JSONObject();
        try {
            mainObj.put("to", userFcmToken);
            JSONObject notiObject = new JSONObject();
            notiObject.put("title", title);
            notiObject.put("body", body);
            notiObject.put("icon", R.drawable.logocompanyicon); // enter icon that exists in drawable only
            mainObj.put("notification", notiObject);


            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj, response -> {
                // code run is got response
            }, error -> {
                // code run is got error
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + fcmServerKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}