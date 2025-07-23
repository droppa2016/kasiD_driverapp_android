package co.za.kasi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import co.za.kasi.R;


public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the received broadcast here
            if (intent.getAction() != null && intent.getAction().equals(context.getString(R.string.ACTION_NOTIFICATION_LISTENER))) {

                showNotificationDialog();
                Toast.makeText(context, "Nottification", Toast.LENGTH_SHORT).show();
            }
        }

        private void showNotificationDialog() {
            // Implement your logic to show the NotificationFragmentDialog
            // You can use a FragmentTransaction to show the dialog
           // Toast.makeText(context, "Nottification", Toast.LENGTH_SHORT).show();
            Log.d("notifications==", "======================================================");
        }

}
