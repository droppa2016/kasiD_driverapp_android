package co.za.kasi.services;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.onesignal.notifications.INotificationClickEvent;
import com.onesignal.notifications.INotificationClickListener;

import org.json.JSONObject;

import co.za.kasi.MainApplication;


public class NotificationOpenHandler implements INotificationClickListener {
    @Override
    public void onClick(@NonNull INotificationClickEvent iNotificationClickEvent) {

        //INotificationA.ActionType actionType = result.getAction().getType();
        JSONObject data = iNotificationClickEvent.getNotification().getAdditionalData();
        String bookingKey, notificationType;
        Log.d("PUSH_TYPE", data.toString());
        Log.d("PUSH_TYPE", data.toString());
        Context context = MainApplication.Companion.getInstance();

        if (data != null) {
            bookingKey = data.optString("bookingOid", null);
            notificationType = data.optString("notificationType", null);

            Log.d("RENTAL_BKNG_DATA", data+" ======");
            Log.d("RENTAL_BKN_OID", bookingKey);
            Log.d("PUSH_TYPE", notificationType);
            Log.i("OneSignalExample", "Booking Oid set with value: " + bookingKey);


        }else{
            Log.d("NO_DATA","=======================");
        }
    }
}
