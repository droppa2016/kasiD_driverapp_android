package co.za.kasi.services;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import co.za.kasi.R;


public class NotificationListenerClass extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if (sbn.getPackageName().equals("co.za.droppa")) {
            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;

            Intent intent = new Intent(getString(R.string.ACTION_NOTIFICATION_LISTENER));

            if (extras != null) {
                Gson gson = new Gson();
                JSONObject data = gson.fromJson(extras.getString("data"), JSONObject.class);;
                Log.e("keys___", "Key:  ======= " + data );
                for (String key : extras.keySet()) {
                    // Retrieve the value associated with the key
                    String value = extras.getString(key);

                    Log.e("keys___", "Key:  = " + extras.getString(key) );
                    Log.d("keys___", "Key: " + key + ", Value: " + value);

                    // Process the key-value pair as needed
                    if (value != null) {
                        // Handle the key-value pair
                        //Log.d("TAG_extra_data", "Key: " + key + ", Value: " + value);
                    } else {
                       // Log.d("TAG_extra_data", "Key: no key" + ", Value: " + value);
                    }
                }

                if (data != null) {
                    intent.putExtra(getString(R.string.extra_data), data.toString());
                    Log.d("TAG_extra_data", "onNotificationPosted: " + data);
                } else {
                    Log.d("TAG_extra_data", "onNotificationPosted: No extra data");
                }

                intent.putExtra(getString(R.string.notification), extras);
                sendBroadcast(intent);
            }

            // Toast.makeText(this, " Notification Arrived " + sbn.getNotification().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        //Toast.makeText(this, " ========= Notification Removed ========= " + sbn.getNotification().toString(), Toast.LENGTH_SHORT).show();
    }


}
