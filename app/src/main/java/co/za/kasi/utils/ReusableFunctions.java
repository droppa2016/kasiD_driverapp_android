package co.za.kasi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.za.kasi.AccountAccessActivity;
import co.za.kasi.FallBackPage;
import co.za.kasi.MainApplication;

import co.za.kasi.R;
import co.za.kasi.adapters.google.CalculateDistanceTime;
import co.za.kasi.adapters.google.GsonRequest;
import co.za.kasi.adapters.google.VolleySingleton;
import co.za.kasi.dialogs.Alert;
import co.za.kasi.dialogs.Loader;
import co.za.kasi.enums.BookingStatus;
import co.za.kasi.enums.PointType;
import co.za.kasi.model.Booking;
import co.za.kasi.model.BookingPoint;
import co.za.kasi.model.CoordsDTOForPolyLines;
import co.za.kasi.model.DirectionObject;
import co.za.kasi.model.DriverLocationTracking;
import co.za.kasi.model.ErrorResponse;
import co.za.kasi.model.FeeDTO;
import co.za.kasi.model.LegsObject;
import co.za.kasi.model.PolylineObject;
import co.za.kasi.model.ProfileDTO;
import co.za.kasi.model.RouteObject;
import co.za.kasi.model.StepsObject;
import co.za.kasi.model.errorDTO.FallBackErrorDTO;
import co.za.kasi.model.systemDTO.WhatsappAccountsDTO;
import co.za.kasi.services.DriverHttpService;
import co.za.kasi.services.LocalStorage;
import co.za.kasi.services.NotificationListenerClass;
import co.za.kasi.services.OfflineAPIInterceptor;
import co.za.kasi.services.SuperAppHttpService;
import co.za.kasi.services.SyncDataService;
import co.za.kasi.services.location.LocationService1;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface ReusableFunctions {

    String DIRECTORY_NAME = "CameraApp/droppa/";
    File DIRECTORY_PATH = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), DIRECTORY_NAME);

    public static void hideORShowPassword(TextInputEditText password, Context context) {

        if (password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.key_blue_password_icon_login), null, context.getDrawable(R.drawable.baseline_visibility_off_24), null);
            password.setSelection(password.getText().toString().length());

        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.key_blue_password_icon_login), null, context.getDrawable(R.drawable.baseline_visibility_24), null);
            password.setSelection(password.getText().toString().length());
        }
    }

    public static void hideORShowPasswordSignUp(TextInputEditText password, Context context) {

        if (password.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.outline_lock_24), null, context.getDrawable(R.drawable.baseline_visibility_off_24), null);
            password.append("");
            password.setSelection(password.length());

        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(context.getDrawable(R.drawable.outline_lock_24), null, context.getDrawable(R.drawable.baseline_visibility_24), null);
            password.append("");
            password.setSelection(password.length());

        }
    }

    public static GestureDetector onGestureDetector(TextInputEditText password, Context context) {

        return new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Handle the single tap event here
                hideORShowPassword(password, context);
                return true;
            }
        });
    }


    public static TranslateAnimation shakeError() {

        TranslateAnimation shake = new TranslateAnimation(0, 5, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(2));
        return shake;

    }

    public static TextWatcher errorRemovingWatcher(TextInputEditText email, Context context) {

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email.setBackground(context.getDrawable(R.drawable.text_background));
                email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static void countDown(MaterialTextView textView) {
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView.setText("resend in: " + millisUntilFinished / 1000);
                // logic to set the EditText could go here
            }

            public void onFinish() {
                textView.setText("Resend OTP");
            }

        }.start();
    }

    public static BitmapDescriptor bitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(
                context, vectorResId);

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable.setBounds(
                0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public static CameraPosition getCamPosition(LatLng currentPos) {
        return new CameraPosition.Builder().target(currentPos).zoom(16.5f).bearing(0).tilt(50).build();
    }

    public static void setUpGoogleMap(GoogleMap map) {
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(true);
        map.setTrafficEnabled(false);
        UiSettings mUiSettings = map.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    public static void markerDelay(Handler mHandler, Runnable mAnimation) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                mHandler.post(mAnimation);
            }
        }, 500);
    }


    public static void openWhatsapp(Context context) {
        /*+27813710961*/
        String url = context.getString(R.string.whatsap_url);
        getWhatsappAccount(context, LocalStorage.getToken(context), url);

    }

    public static void openDialer(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        context.startActivity(intent);
    }

    public static Loader showLoader(Context context) {
        Loader loader = new Loader(context);
        loader.show();
        return loader;
    }

    public static void hideLoader(Loader loader) {
        loader.dismiss();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isNotificationListenerServiceEnabled(Context context) {
        String packageName = context.getPackageName();
        String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        return flat != null && flat.contains(packageName + "/" + NotificationListenerClass.class.getName());
    }

    public static void checkAndRequestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Request the permission
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1001
                );
            }
        }
    }

    public static void handleBlockedUsers(Activity activity) {
        LocalStorage.storeSkynetDriverAccount(null);
        LocalStorage.storeIsUserBlocked(true);

        Intent serviceIntent = new Intent(activity, LocationService1.class);
        serviceIntent.setAction(LocationService1.ACTION_STOP);
        activity.startService(serviceIntent);

        Intent intent = new Intent(activity, AccountAccessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
        activity.finish();
    }


    public static DriverHttpService initiateRetrofit(Context context) {

        Log.d("TAG_base_url", "initiateRetrofit: " + getBaseUrl(context));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BaseUrl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DriverHttpService.class);
    }

    public static SuperAppHttpService initiateSuperAppRetrofit(Context context) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OfflineAPIInterceptor(context))
                .build();

        Log.d("TAG_base_url", "initiateRetrofit: " + getBaseUrl(context));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BaseUrl))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(SuperAppHttpService.class);
    }

    static SyncDataService initiateSyncDataRetrofit(Context context) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OfflineAPIInterceptor(context))
                .build();

        Log.d("TAG_base_url", "initiateRetrofit: " + getBaseUrl(context));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.BaseUrl))
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(SyncDataService.class);
    }

    public static DriverHttpService initiateRetrofitWithFirebase(Context context) {

        Log.d("TAG_base_url", "initiateRetrofit: " + getBaseUrl(context));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getFirebaseBaseUrl(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(DriverHttpService.class);
    }

    public static void initiateFallBackPage(Context context, String activityName, FallBackErrorDTO error) {
        Intent intent = new Intent(context, FallBackPage.class);
        intent.putExtra("class", activityName);
        intent.putExtra("data", error);
        context.startActivity(intent);
    }

    public static Snackbar snackbarInstance(View view, String message, int duration, int backgroundColor, int textColor, int gravity) {

        Snackbar snackbar = Snackbar.make(view, message, duration);
        View snackbarView = snackbar.getView();
        snackbar.setBackgroundTint(backgroundColor);
        snackbar.setTextColor(textColor);
        try {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = gravity;
            snackbarView.setLayoutParams(params);
        } catch (Exception e) {
            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = gravity;
            snackbarView.setLayoutParams(params);

        }

        // Set the adjusted layout parameters
        return snackbar;
    }

    public static void dismisSnackBar(Snackbar snackbar) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, 2200);
    }

    public static void errorHandler(Context context, ErrorResponse errorResponse, int code, View view, int colorBackground, int color, Snackbar snackbar, String activity) {

        if (code == 400) {
            snackbar = snackbarInstance(view, errorResponse.getMessage(), Snackbar.LENGTH_INDEFINITE, colorBackground, color, Gravity.BOTTOM);
            snackbar.show();
            ReusableFunctions.dismisSnackBar(snackbar);
        } else {
            Bundle args = new Bundle();
            args.putString("class", activity);
            args.putString("error", errorResponse.getMessage());
            context.startActivity(new Intent(context, FallBackPage.class), args);
        }
    }

   /* @SuppressLint("ResourceType")
    public static Snackbar snackbarInstance(View view, String message, int duration, int backgroundColor, Drawable iconRes) {

        Snackbar snackbar = Snackbar.make(view, message, duration);
        snackbar.getView().setBackgroundResource(R.layout.snack_bar_layout);
        AppCompatImageView icon = snackbar.getView().findViewById(R.id.snackbar_icon);
        CardView root = snackbar.getView().findViewById(R.id.root);

        root.setCardBackgroundColor(backgroundColor);
        icon.setImageDrawable(iconRes);

        return snackbar;
    }*/

    public static ErrorResponse convertErrorResponse(String error) {
        Gson gson = new Gson();
        return gson.fromJson(error, ErrorResponse.class);
    }


    public static Date decodeTime(String time) throws ParseException {
        SimpleDateFormat sm = new SimpleDateFormat("hh:mm");

        return sm.parse(time);
    }

    public static Date decodeDate(String date) throws ParseException {
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");

        return sm.parse(date);
    }

    public static Date standardDateFormat(String date, String time) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm a", Locale.ENGLISH);

        return formatter.parse(date + " " + time);
    }

    public static Date getTodaysdate() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return date;
    }

    public static boolean compareDates(Date date) {

        Log.d("Possitipon__", date + " , //// , " + getTodaysdate());

        int result = date.compareTo(getTodaysdate());
        if (result == 0) {
            Log.d("Possitipon__", " , //// ,  true");
            return true;
        } else {
            Log.d("Possitipon__", " , //// ,  false");
            return false;
        }
    }

    public static String decimalFormater(double amnt) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(amnt);
    }

    public static String getstandardDateFormat(String date) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd hh:mm a", Locale.ENGLISH);
        Date newdate = formatter.parse(date);
        return formatter.format(newdate);
    }

    public static Date convertStringToDate(String _date) {
        Log.d("Possitipon__", _date + " , //// , ");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        try {
            Date date = dateFormat.parse(_date);

            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String getDistanceOld(Context context, LatLng latLngStart, LatLng latLngEnd) {
        final String[] time = {""};
        CalculateDistanceTime distance_task = new CalculateDistanceTime(context, PropertyReader.getPropertyValue(context, context.getString(R.string.MAPS_API_KEY)));

        distance_task.getDirectionsUrl(latLngStart, latLngEnd);

        distance_task.setLoadListener(time_distance -> {
            time[0] = time_distance[0];
        });
        return null;
    }

    private static String getBaseUrl(Context context) {
        return PropertyReader.getPropertyValue(context, context.getString(R.string.property_base_url));
    }

    private static String getFirebaseBaseUrl(Context context) {
        return PropertyReader.getPropertyValue(context, context.getString(R.string.firebase_url));
    }

    public static String getOneSignalID(Context context) {
        return PropertyReader.getPropertyValue(context, context.getString(R.string.property_onesignal_app_id));
    }

    public static String getGoogleMapKey(Context context) {
        return PropertyReader.getPropertyValue(context, context.getString(R.string.MAPS_API_KEY));
    }

    public static void getWhatsappAccount(Context context, String token, String url) {

        Call<WhatsappAccountsDTO> getWhatsappAccountCall = initiateRetrofit(context).getActiveWhatsappAccounts(token);

        getWhatsappAccountCall.enqueue(new Callback<WhatsappAccountsDTO>() {
            @Override
            public void onResponse(Call<WhatsappAccountsDTO> call, Response<WhatsappAccountsDTO> response) {
                if (response.code() == 200) {

                    response.body().getDriver();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.setData(Uri.parse(url + "+27" + response.body().getDriver().substring(1)));
                    context.startActivity(i);
                }
            }

            @Override
            public void onFailure(Call<WhatsappAccountsDTO> call, Throwable t) {

            }
        });

    }

    public static CoordsDTOForPolyLines getCoords(ArrayList<Booking> bookings, boolean isInbound, boolean isDash) {

        ArrayList<BookingPoint> pickupCoords = new ArrayList<>();
        ArrayList<BookingPoint> dropOffCoords = new ArrayList<>();

        CoordsDTOForPolyLines polyLinesPoins = new CoordsDTOForPolyLines();

        if (!isDash) {
            if (!isInbound) {
                pickupCoords.add(0, getPickUpPoint(bookings.get(0)));
            } else {
                dropOffCoords.add(0, getDropOffPoint(bookings.get(0)));
            }

            for (Booking booking : bookings) {

                if (!booking.getStatus().equals(BookingStatus.COMPLETE.name())) {
                    if (!isInbound) {
                        if (booking.isItemPicked() && !booking.getStatus().equalsIgnoreCase(BookingStatus.COMPLETE.name())) {
                            dropOffCoords.add(0, getDropOffPoint(booking));
                        }
                    } else {
                        if (booking.isItemPicked()) {
                            pickupCoords.add(0, getPickUpPoint(booking));
                        }
                    }
                }

            }
        } else {

            for (Booking booking : bookings) {

                if (!booking.getStatus().equals(BookingStatus.COMPLETE.name())) {
                    if (booking.isItemPicked()) {
                        dropOffCoords.add(0, getDropOffPoint(booking));
                    }

                    if (!booking.isItemPicked()) {

                        pickupCoords.add(0, getPickUpPoint(booking));
                    }
                }

            }

        }

        polyLinesPoins.setDroppOffPoints(dropOffCoords);
        polyLinesPoins.setPickUpPoints(pickupCoords);


        return polyLinesPoins;
    }

    public static Alert showAlert(Context context, String title, String message, String type) {

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", message);
        args.putString("type", type);

        Alert alertDialog = new Alert(context);
        alertDialog.setArguments(args);

        return alertDialog;
    }

    private static BookingPoint getPickUpPoint(Booking booking) {
        BookingPoint pickUp = new BookingPoint();
        pickUp.setBookingOid(booking.getOid());
        pickUp.setPointType(PointType.PICK_UP.name());
        pickUp.setPicked(booking.isItemPicked());
        pickUp.setTrackNo(booking.getTrackNo());
        pickUp.setPickup(booking.getPickUp());
        pickUp.setBucket(booking.isBucket());

        pickUp.setInstructions(booking.getComment());
        pickUp.setClientPhone(booking.getPickUp().getPhone());
        pickUp.setClientName(booking.getPickUp().names());

        pickUp.setStatus(BookingStatus.AWAITING_DRIVER.name());
        pickUp.setLatitude(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[0]));
        pickUp.setLongitude(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[1]));

        pickUp.setAddress(booking.getPickUpAddress());

        return pickUp;
    }

    private static BookingPoint getDropOffPoint(Booking booking) {
        BookingPoint dropOff = new BookingPoint();
        dropOff.setBookingOid(booking.getOid());
        dropOff.setPointType(PointType.DROP_OFFS.name());
        dropOff.setStatus(booking.getStatus());
        dropOff.setTrackNo(booking.getTrackNo());

        dropOff.setBucket(booking.isBucket());
        dropOff.setPicked(false);

        dropOff.setTransportType(booking.getTransportMode());
        dropOff.setType(booking.getType());
        dropOff.setDropoff(booking.getDropOff());

        dropOff.setClientPhone(booking.getDropOff().getPhone());
        dropOff.setClientName(booking.getDropOff().names());
        dropOff.setInstructions(booking.getComment());
        dropOff.setLatitude(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[0]));
        dropOff.setLongitude(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[1]));

        dropOff.setAddress(booking.getDropOffAddress());


        return dropOff;
    }

    public static ArrayList<BookingPoint> getBookingPoints(ArrayList<Booking> bookings, Location location) {

        ArrayList<BookingPoint> dropOffs = new ArrayList<>();

        for (Booking booking : bookings) {

            if (!booking.getStatus().equals(BookingStatus.COMPLETE.name()) && booking.isBucket()) {
                //Set Pick-Ups
                BookingPoint pickUp = new BookingPoint();
                pickUp.setBookingOid(booking.getOid());
                pickUp.setPointType(PointType.PICK_UP.name());
                pickUp.setPicked(booking.isItemPicked());
                pickUp.setTrackNo(booking.getTrackNo());
                pickUp.setPickup(booking.getPickUp());
                pickUp.setBucket(booking.isBucket());

                pickUp.setInstructions(booking.getComment());
                pickUp.setClientPhone(booking.getPickUp().getPhone());
                pickUp.setClientName(booking.getPickUp().names());

                pickUp.setStatus(BookingStatus.AWAITING_DRIVER.name());
                pickUp.setLatitude(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[0]));
                pickUp.setLongitude(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[1]));
                pickUp.setBooking(booking);
                pickUp.setAddress(booking.getPickUpAddress());
                dropOffs.add(pickUp);

                //Set drop-off
                BookingPoint dropOff = new BookingPoint();
                dropOff.setBookingOid(booking.getOid());
                dropOff.setPointType(PointType.DROP_OFFS.name());
                dropOff.setStatus(booking.getStatus());
                dropOff.setTrackNo(booking.getTrackNo());

                dropOff.setBucket(booking.isBucket());
                dropOff.setPicked(false);

                dropOff.setTransportType(booking.getTransportMode());
                dropOff.setType(booking.getType());
                dropOff.setDropoff(booking.getDropOff());

                dropOff.setClientPhone(booking.getDropOff().getPhone());
                dropOff.setClientName(booking.getDropOff().names());
                dropOff.setInstructions(booking.getComment());
                dropOff.setLatitude(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[0]));
                dropOff.setLongitude(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[1]));
                dropOff.setBooking(booking);
                dropOff.setAddress(booking.getDropOffAddress());

                dropOffs.add(dropOff);

            }

        }
        Log.d("BUCKTBKNGSTATUS", "====== STATUS ==" + dropOffs);
        return sortedBookingPoints(dropOffs, location);
    }

    public static ArrayList<BookingPoint> sortedBookingPoints(ArrayList<BookingPoint> list, Location driverLocation) {
        BookingPoint temp = null;
        float distanceA;
        float distanceB;
        Location dropLocationA = new Location("");
        Location dropLocationB = new Location("");
        if (driverLocation == null)
            driverLocation = LocalStorage.getDriverLocation(MainApplication.Companion.getInstance());

        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < (list.size() - i - 1); j++) {

                dropLocationA.setLongitude(list.get(j).getLongitude());
                dropLocationA.setLatitude(list.get(j).getLatitude());

                dropLocationB.setLongitude(list.get(j + 1).getLongitude());
                dropLocationB.setLatitude(list.get(j + 1).getLatitude());

                distanceA = driverLocation.distanceTo(dropLocationA) / 1000;
                distanceB = driverLocation.distanceTo(dropLocationB) / 1000;

                if (distanceA > distanceB) {
                    //swap the elements!
                    temp = list.get(j + 1);

                    list.set(j + 1, list.get(j));
                    list.set(j, temp);
                }

            }
        }

        Log.d("BUCKTBKNGSTATUS", "====== STATUS == sorted " + list);
        return list;
    }

    public static void updateDriverLocation() {
//        Location current = LocalStorage.getLocation();
//
        DriverLocationTracking tracking = new DriverLocationTracking();
//
//        //String status = Objects.requireNonNull(OneSignal.getDeviceState()).getUserId();
//
//        //OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
//        //String oneSignalID = status.getSubscriptionStatus().getUserId();
//
//       // String oneSignalID = Objects.requireNonNull(OneSignal.getSession().getDeviceState()).getUserId();
//
//        JSONObject owner = null;
//        try {
//            JSONObject driverData = new JSONObject(Objects.requireNonNull(sharedPref.getString(CustomApplication.getInstance().getString(R.string.user_data), null)));
//            owner = new JSONObject(driverData.getString("owner"));
//            JSONObject driver = new JSONObject(Objects.requireNonNull(sharedPref.getString(CustomApplication.getInstance().getString(R.string.driver_data), null)));
//            String vehicleType = driver.getJSONObject("vehicle").getString("type");
//            String driverOid = driver.getString("oid");
//            String province = driver.getString("province");
//
//            String name = owner.getString("firstName") + " " + owner.getString("surname");
//
//            tracking.setDriverName(name);
//            phoneNumber = owner.getString("mobile");
//            tracking.setDriverPhone(phoneNumber);
//            tracking.setProvice(province);
//            tracking.setVehicleType(vehicleType);
//            tracking.setDriverOid(driverOid);
//
//        } catch (Exception e) {
//            Log.d("NICOLAS", "" + e.getMessage());
//        }
//
//
//        tracking.setOneSignalId(oneSignalID);
//        tracking.setLocation(current);
//        tracking.setMainCityOid(getPointOfInterest () != null ? getPointOfInterest ().getOid() : null);
//
//        if(getPointOfInterest () != null)
//            OneSignal.sendTag("pointOfInterest", getPointOfInterest().getOid());
//
//        if(SharedPreferencesUtil.getExpressBookings() != null &&
//                !SharedPreferencesUtil.getExpressBookings().isEmpty()) {
//            DatabaseReference driverTrackLoc = FirebaseDatabase.getInstance().
//                    getReference("driverTracking");
//            DriverTracking driverTracking = new DriverTracking();
//
//            driverTracking.setLat(getDriverLocation().getLatitude());
//            driverTracking.setLng(getDriverLocation().getLongitude());
//            driverTracking.setBearing(getDriverLocation().getBearing());
//
//            driverTrackLoc.child(phoneNumber).
//                    setValue(driverTracking);
//
//            Log.e("NICOLAS", "******************************************* Firebase location updated -----------");
//        } else {
//            Log.e("NICOLAS", "******************************************* No Firebase location update -----------");
//        }
        DriverHttpService services = initiateRetrofit(MainApplication.Companion.getInstance());

        Call<DriverLocationTracking> accountCall = services.updateLocation(tracking);
        accountCall.enqueue(new Callback<DriverLocationTracking>() {
            @Override
            public void onResponse(@NonNull Call<DriverLocationTracking> call, @NonNull Response<DriverLocationTracking> response) {
                Log.d("NICOLAS", "" + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<DriverLocationTracking> call, @NonNull Throwable t) {
            }
        });
    }


    public static void dismissAlert(Alert alert) {
        alert.dismiss();
    }

    public static ArrayList<Booking> sortedBookings(ArrayList<Booking> list, Location driverLocationm) {
        Booking temp = null;
        float distanceA;
        float distanceB;
        Location dropLocationA = new Location("");
        Location dropLocationB = new Location("");
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < (list.size() - i - 1); j++) {

                dropLocationA.setLongitude(Double.parseDouble(list.get(j).getDropOffCoordinate().getCoordinates()[1]));
                dropLocationA.setLatitude(Double.parseDouble(list.get(j).getDropOffCoordinate().getCoordinates()[0]));

                dropLocationB.setLongitude(Double.parseDouble(list.get(j + 1).getDropOffCoordinate().getCoordinates()[1]));
                dropLocationB.setLatitude(Double.parseDouble(list.get(j + 1).getDropOffCoordinate().getCoordinates()[0]));

                distanceA = driverLocationm.distanceTo(dropLocationA) / 1000;
                distanceB = driverLocationm.distanceTo(dropLocationB) / 1000;

                if (distanceA > distanceB) {
                    //swap the elements!
                    temp = list.get(j + 1);

                    list.set(j + 1, list.get(j));
                    list.set(j, temp);
                }

            }
        }
        return list;
    }


//    public static CoordsDTOForPolyLines getCoords(ArrayList<Booking> bookings, boolean isInbound) {
//
//        ArrayList<LatLng> pickupCoords = new ArrayList<>();
//        ArrayList<LatLng> dropOffCoords = new ArrayList<>();
//
//        CoordsDTOForPolyLines polyLinesPoins = new CoordsDTOForPolyLines();
//
//        if (!isInbound) {
//            pickupCoords.add(0, new LatLng(Double.parseDouble(bookings.get(0).getPickUpCoordinate().getCoordinates()[0]), Double.parseDouble(bookings.get(0).getPickUpCoordinate().getCoordinates()[1])));
//            Log.d("TAG_coords", "showAllMarker: pick_up single" + pickupCoords);
//        } else {
//            dropOffCoords.add(0, new LatLng(Double.parseDouble(bookings.get(0).getDropOffCoordinate().getCoordinates()[0]), Double.parseDouble(bookings.get(0).getDropOffCoordinate().getCoordinates()[1])));
//            Log.d("TAG_coords", "showAllMarker: dropp_off single" + dropOffCoords);
//        }
//
//        for (Booking booking : bookings) {
//
//            if (!booking.getStatus().equals(BookingStatus.COMPLETE.name()) || !booking.getStatus().equals(BookingStatus.COMPLETE.description())) {
//                if (!isInbound) {
//                    if(booking.isItemPicked()) {
//                        dropOffCoords.add(new LatLng(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[0]), Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[1])));
//                        Log.d("TAG_coords", "showAllMarker: dropp_off multiple" + pickupCoords);
//                    } else {
//                        dropOffCoords.add(new LatLng(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[0]), Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[1])));
//                        Log.d("TAG_coords", "showAllMarker: dropp_off multiple" + pickupCoords);
//                    }
//                } else {

    /// /                    if(!booking.isItemPicked()) {
    /// /                        pickupCoords.add(new LatLng(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[0]), Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[1])));Log.d("TAG_coords", "showAllMarker: pick_up single" + pickupCoords);
    /// /                        Log.d("TAG_coords", "showAllMarker: pick_up multiple" + pickupCoords);
    /// /                    }
//                    if(booking.isItemPicked()) {
//                        dropOffCoords.add(new LatLng(Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[0]), Double.parseDouble(booking.getDropOffCoordinate().getCoordinates()[1])));
//                        Log.d("TAG_coords", "showAllMarker: dropp_off multiple" + pickupCoords);
//                    } else {
//                        dropOffCoords.add(new LatLng(Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[0]), Double.parseDouble(booking.getPickUpCoordinate().getCoordinates()[1])));
//                        Log.d("TAG_coords", "showAllMarker: dropp_off multiple" + pickupCoords);
//                    }
//                }
//            }
//
//        }
//
//        polyLinesPoins.setDroppOffPoints(dropOffCoords);
//        polyLinesPoins.setPickUpPoints(pickupCoords);
//
//
//        return polyLinesPoins;
//    }


    /* ==========================================================+===*/
    /*coords*/
    static void getDirectionFromDirectionApiServer(Context context, String url, GoogleMap mMap) {
        GsonRequest<DirectionObject> serverRequest = new GsonRequest<>(
                Request.Method.GET,
                url,
                DirectionObject.class,
                createRequestSuccessListener(context, mMap),
                createRequestErrorListener());
        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(serverRequest);

    }

    private static com.android.volley.Response.Listener<DirectionObject> createRequestSuccessListener(Context context, GoogleMap mMap) {
        return response -> {
            try {
                Log.d("JSON Response", response.toString());
                if (response.getStatus().equals("OK")) {
                    List<LatLng> mDirections = getDirectionPolylines(response.getRoutes());
                    drawRouteOnMap(context, mMap, mDirections);
                } else {
                    //Toast.makeText(AttendBookingActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    private static com.android.volley.Response.ErrorListener createRequestErrorListener() {
        return Throwable::printStackTrace;
    }

    private static List<LatLng> getDirectionPolylines(List<RouteObject> routes) {
        List<LatLng> directionList = new ArrayList<>();
        for (RouteObject route : routes) {
            List<LegsObject> legs = route.getLegs();
            for (LegsObject leg : legs) {
                List<StepsObject> steps = leg.getSteps();
                for (StepsObject step : steps) {
                    PolylineObject polyline = step.getPolyline();
                    String points = polyline.getPoints();
                    List<LatLng> singlePolyline = decodePoly(points);
                    directionList.addAll(singlePolyline);
                }
            }
        }
        return directionList;
    }

    private static void drawRouteOnMap(Context context, GoogleMap map, List<LatLng> positions) {
        PolylineOptions options = new PolylineOptions().width(15).color(context.getColor(R.color.skynet_color)).geodesic(true);
        options.addAll(positions);
        Polyline polyline = map.addPolyline(options);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(positions.get(1).latitude, positions.get(1).longitude))
                .zoom(15)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private static List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
    /*coords*/
    /* ================================================================*/


    public static String getDistance(Context context, LatLng latLngStart, LatLng latLngEnd) {

        final String[] timeDistance = {null};
        CalculateDistanceTime distance_task = new CalculateDistanceTime(context, getGoogleMapKey(context));

        distance_task.getDirectionsUrl(latLngStart, latLngEnd);

        distance_task.setLoadListener(time_distance -> {
            timeDistance[0] = time_distance[0].toString();
        });
        return timeDistance[0];
    }

    public static String getUserAddress(Context context, LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            return address;
        } catch (Exception e) {
            return "No address name found.";
        }

    }

    public static Intent navigate(LatLng from, LatLng to) {
        String current = Uri.parse(getUserAddress(MainApplication.Companion.getInstance(), from)).getEncodedPath();
        String toLocation = Uri.parse(getUserAddress(MainApplication.Companion.getInstance(), to)).getEncodedPath();

        String url = "https://www.google.com/maps/dir/?api=1&origin=" + current + "&destination=" + toLocation + "&travelmode=driving";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse(url));
        return intent;
    }

    public static ArrayList<FeeDTO>[] getFees(Context context, String token) {
        final ArrayList<FeeDTO>[] res = new ArrayList[]{new ArrayList<>()};
        Call<ArrayList<FeeDTO>> getFee = initiateRetrofit(context).getFees(token);

        getFee.enqueue(new Callback<ArrayList<FeeDTO>>() {
            @Override
            public void onResponse(Call<ArrayList<FeeDTO>> call, Response<ArrayList<FeeDTO>> response) {

                if (response.code() == 200) {

                    Log.e("fees", "---------------------------FEE value at 0 -" + response.body().get(0).getValue());
                    Log.d("fees", "---------------------------FEE value at 1 -" + response.body().get(1).getValue());
                    Log.e("fees", "---------------------------FEE type " + response.body().get(0).getType());

                    res[0] = response.body();


                } else {

                    try {
                        Log.d("fee", "---------------------------FEE else is called" + response.errorBody().string());
                        Log.d("fee", "---------------------------FEE else is called" + response.code());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

            @Override
            public void onFailure(Call<ArrayList<FeeDTO>> call, Throwable t) {

                Log.d("fee", "---------------------------FEE onfailure is called" + t.getMessage());
            }
        });

        return res;
    }

    public static String getNetPrice(double price) {
        ArrayList<FeeDTO> standardFees = new ArrayList<>();

        standardFees = LocalStorage.getStandardFees();
        double vatFreeAmount = price - (price / standardFees.get(1).getValue());
        double droppaFreeAmount = (price - vatFreeAmount) * standardFees.get(0).getValue();

        Log.d("TAG_Ammounts", "vatFreeAmount: " + price);
        Log.d("TAG_Ammounts", "vatFreeAmount: " + vatFreeAmount);
        Log.d("TAG_Ammounts", "feeFreeAmount: " + droppaFreeAmount);
        return decimalFormater(price - (vatFreeAmount + droppaFreeAmount));
    }


    public static void getProfilePic(String token, String ownerID, Context context) {

        Call<ProfileDTO> getProfileCall = initiateRetrofit(context).getProfile(token, ownerID);

        getProfileCall.enqueue(new Callback<ProfileDTO>() {
            @Override
            public void onResponse(Call<ProfileDTO> call, Response<ProfileDTO> response) {
                if (response.code() == 200) {
                    try {
                        Log.d("TAG_avater", "onResponse: " + response.body().toString());
                        LocalStorage.storeProfile(response.body());
                    } catch (Exception e) {
                        e.getStackTrace();

                    }
                } else {

                    try {
                        Log.e("TAG_avater", "onResponse: " + response.errorBody().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileDTO> call, Throwable t) {

                Log.e("TAG_avater", "onResponse: " + t.getMessage(), t);
            }
        });
    }

    public static void clearFiles() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), DIRECTORY_NAME);
        File directory = new File(DIRECTORY_PATH.toURI());

        // Check if the directory exists
        if (directory.exists() && directory.isDirectory()) {
            // List all files in the directory
            File[] files = directory.listFiles();

            // Check if there are files to delete
            if (files != null) {
                for (File file : files) {
                    // Delete each file
                    file.delete();
                }
            }
        }
    }

    static String getFirstOfMonthFormatted(String dateString) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate date = LocalDate.parse(dateString, formatter);
            LocalDate firstOfMonth = date.getDayOfMonth() == 1 ? date : date.withDayOfMonth(1);
            return firstOfMonth.format(formatter);
        }
        return dateString;
    }

    static String getFormattedDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }


}
