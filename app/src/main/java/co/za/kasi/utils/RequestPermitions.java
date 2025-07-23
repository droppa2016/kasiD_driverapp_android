package co.za.kasi.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class RequestPermitions {

    private static int PERMISTION_CODE = 101;
    int requestCode;
    String[] permissions;
    int[] grantResults;
    Context context;
    Activity activity;

    public RequestPermitions(int requestCode, String[] permissions, int[] grantResults, Context context, Activity activity) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
        this.context = context;
        this.activity = activity;

        Log.d("TAG_grant_results", "RequestPermitions: grantResults == " + grantResults + "RequestPermitions: permistions == " + grantResults);

        getCameraPermission();
        getAllPermissions();
    }

    public void getCameraPermission() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS}, PERMISTION_CODE);
                    }
                }
            } else {
                if (grantResults.length > 0) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS}, PERMISTION_CODE);
                    }
                }
            }

        } else {
            if (grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.POST_NOTIFICATIONS}, PERMISTION_CODE);
                }
            }
        }

    }


    public void getAllPermissions() {
        if (grantResults.length > 0) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.FOREGROUND_SERVICE_LOCATION}, PERMISTION_CODE);
                }
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.FOREGROUND_SERVICE_LOCATION}, PERMISTION_CODE);
            }
        }

    }

}
