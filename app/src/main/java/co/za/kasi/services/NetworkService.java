package co.za.kasi.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;

public class NetworkService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.post(delay);
        return START_STICKY;
    }

    public boolean isConnected(Context context){

        ConnectivityManager cm = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback();

        /* return cm.getActiveNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED;*/

        if (cm.getActiveNetworkInfo()!=null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) return true;
            else return false;

    }

    Handler handler = new Handler();
    private Runnable delay = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(delay, 1*1000- SystemClock.elapsedRealtime()%1000);
            Intent broadcastIntent = new Intent();
            /*broadcastIntent.setAction(MainActivity.BroadcastStringForAction);*/
            broadcastIntent.putExtra("isOnline","" + isConnected(NetworkService.this));
            sendBroadcast(broadcastIntent);

        }
    };

}
