package co.za.kasi.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.za.kasi.R;


public class RunningService extends Service {

    Location gps_loc;
    Location network_loc;
    Location final_loc;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("NICOLAS", "start in service");
        initLocation ();
        return START_STICKY;
    }

    private long timerTime = TimeUnit.SECONDS.toMillis(300);
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("NICOLAS", "******************************************* I am called -----------");

                initLocation ();
               // LocalStorage.updateDriverLocation();



            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                timerHandler.postDelayed(this, timerTime);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            startForeground(62318, builtNotification());
            timerHandler.postDelayed(timerRunnable, 0);
        }catch (Exception e){
            Log.e("ERROR_TEST", e.getMessage());
        }
    }

    public void stopHandler () {
        timerHandler.removeCallbacks(timerRunnable);
//        if (sharedPref.getString(MainApplication.getInstance().getString(R.string.user_email), null) != null) {
//            ForegroundServiceLauncher.getInstance().startService(this);
//        }
    }


    public Notification builtNotification()
    {


        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;

        NotificationCompat.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel("ID", "Name", importance);

            //notificationChannel.setSound(soundUri,audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this, notificationChannel.getId());
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        //builder.setSound(soundUri);

        String message = "Driver online for bookings.";
        builder.setSmallIcon(R.drawable.ic_stat_onesignal_default)
                //.setLargeIcon(BitmapFactory.decodeResource(CustomApplication.getInstance().getResources(),
                //        R.drawable.ic_stat_onesignal_default))
                .setAutoCancel(false)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setColor(Color.parseColor("#0399D1"))
                .setContentTitle(LocalStorage.getDriverAccount().getPerson().getFirstName())
                .setContentText(message);

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //
        stopHandler ();
    }

    protected void updateLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }

        FusedLocationProviderClient fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if(location!=null) {
                //Write your implemenation here
                String address = getUserAddress(location.getLatitude(), location.getLongitude());
//                saveStreetName(address);
//                saveCoordinatesInPreferences((float) location.getLatitude(), (float) location.getLongitude());
                Log.e("NICOLAS",location.getLatitude()+" "+location.getLongitude());
            } else {
                Log.e("NICOLAS","No location found");
            }
        });
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {}
    }

    public String getUserAddress(double lat, double longi) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, longi, 1);
            String address = addresses.get(0).getAddressLine(0);
            return address;
        } catch (Exception e) {
            return "No address name found.";
        }
    }



    private void initLocation () {
        getNetworkLocation ();
        Wherebouts.instance().onChange( new Workable<GPSPoint>() {
            @Override
            public void work(GPSPoint gpsPoint) {
                // draw something in the UI with this new data
                if(gpsPoint != null) {
                    String address = getUserAddress(gpsPoint.getLatitude(), gpsPoint.getLongitude());
                    LocalStorage.saveStreetName(address);
                    LocalStorage.saveCoordinatesInPreferences(getBaseContext(), (float) gpsPoint.getLatitude(), (float) gpsPoint.getLongitude());
                    Log.e("NICOLAS", "******************************************* I am called -----------" +gpsPoint.toString());

                } else {
                    getNetworkLocation ();
                }

            }
        });
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        //create an intent that you want to start again.
        getNetworkLocation ();
        super.onTaskRemoved(rootIntent);
    }

    @SuppressLint("MissingPermission")
    private void getNetworkLocation () {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            final_loc = gps_loc;
            Log.e("NICOLAS", "GPS Loc ( -----------"+final_loc.getLatitude() + ", "+final_loc.getLongitude());
            LocalStorage.saveCoordinatesInPreferences(getBaseContext(),(float) final_loc.getLatitude(), (float) final_loc.getLongitude());
        }
        else if (network_loc != null) {
            final_loc = network_loc;
            Log.e("NICOLAS", "Network Loc ( -----------"+final_loc.getLatitude() + ", "+final_loc.getLongitude());
            LocalStorage.saveCoordinatesInPreferences(getBaseContext() ,(float) final_loc.getLatitude(), (float) final_loc.getLongitude());
        }
        else {
            Log.e("NICOLAS", "No location found, i repeat...");
        }
    }
}
