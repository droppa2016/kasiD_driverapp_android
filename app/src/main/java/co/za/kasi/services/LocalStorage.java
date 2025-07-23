package co.za.kasi.services;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.provider.Settings;

import co.za.kasi.MainApplication;

import co.za.kasi.R;
import co.za.kasi.model.FeeDTO;
import co.za.kasi.model.LocationDTO;
import co.za.kasi.model.ProfileDTO;
import co.za.kasi.model.accountDTO.AutoLoginDTO;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse;
import co.za.kasi.model.superApp.a.vehicle.SkynetVehicle;

public interface LocalStorage {

    static Context context = MainApplication.Companion.getInstance();
    static SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.local_DB), MODE_PRIVATE);

    String profilePicture = " ";


    SharedPreferences.Editor editor = sharedPreferences.edit();

    public static void storeFirstVisit(boolean status) {
        editor.putBoolean(context.getString(R.string.Fist_time_visit), status);
        editor.commit();
    }

    public static void storeAdvisedUpdateLastShow(Long currentTime) {
        editor.putLong("advised_update_last_shown", currentTime);
        editor.commit();
    }

    public static Long getAdvisedUpdateLastShow() {
        return sharedPreferences.getLong("advised_update_last_shown", 0);
    }

    public static void storeAppVersionCode(String versionCode) {
        editor.putString("app_version_code", versionCode);
        editor.commit();
    }

    public static String getAppVersionCode() {
        return sharedPreferences.getString("app_version_code", "1.0.0");
    }

     static final String DEVICE_ID_KEY = "device_id";

    public static String getOrCacheAndroidId(Context context) {
        String cachedId = sharedPreferences.getString(DEVICE_ID_KEY, null);

        if (cachedId != null) {
            return cachedId;
        } else {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            sharedPreferences.edit().putString(DEVICE_ID_KEY, androidId).apply();
            return androidId;
        }
    }

    public static void storeFcmToken(String fcmToken) {
        editor.putString("fcm_token", fcmToken);
        editor.commit();
    }

    public static String getFcmToken() {
        return sharedPreferences.getString("fcm_token", null);
    }

    public static void storeSelectedVehicle(SkynetVehicle skynetVehicle) {
        Gson gson = new Gson();
        String object = gson.toJson(skynetVehicle);
        editor.putString(context.getString(R.string.selected_vehicle), object);
        editor.commit();
    }

    public static SkynetVehicle getSelectedVehicle() {
        Gson gson = new Gson();
        String selectedDriver = sharedPreferences.getString(context.getString(R.string.selected_vehicle), null);
        return gson.fromJson(selectedDriver, SkynetVehicle.class);
    }


    public static boolean canGetBookings() {
        return sharedPreferences.getBoolean(context.getString(R.string.Fist_time_visit), false);
    }

    public static boolean getDailyDriverTripStatus() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String lastUpdated = sharedPreferences.getString("myFlagLastUpdated", null);

        if (!today.equals(lastUpdated)) {
            editor.putBoolean("driverStartedTrip", false)
                    .putString("myFlagLastUpdated", today)
                    .apply();
        }

        return sharedPreferences.getBoolean("driverStartedTrip", false);
    }

    public static void setDailyDriverTripStatus(boolean value) {
        context.getSharedPreferences("driverStartedTrip", Context.MODE_PRIVATE);
        editor.putBoolean("driverStartedTrip", value).apply();
    }

    public static void storeCanGetBookings(boolean status) {
        editor.putBoolean(context.getString(R.string.Fist_time_visit), status);
        editor.commit();
    }

    public static boolean verifyFirstVisit() {
        return sharedPreferences.getBoolean(context.getString(R.string.Fist_time_visit), false);
    }

    public static UserDTO storeUserAccouont(UserDTO userDTO) {
        Gson gson = new Gson();
        String object = gson.toJson(userDTO);
        editor.putString(context.getString(R.string.user_account), object);
        editor.commit();

        return gson.fromJson(object, UserDTO.class);
    }

    public static SkynetDriverAppLoginBodyResponse storeSkynetDriverAccount(SkynetDriverAppLoginBodyResponse skynetDriverAppLoginBodyResponse) {
        Gson gson = new Gson();
        String object = gson.toJson(skynetDriverAppLoginBodyResponse);
        editor.putString(context.getString(R.string.skynet_driver_account), object);
        editor.commit();

        return gson.fromJson(object, SkynetDriverAppLoginBodyResponse.class);
    }

    public static SkynetDriverAppLoginBodyResponse storeSkynetDriverVehicle(SkynetVehicle updatedVehicle) {
        Gson gson = new Gson();

        // Get existing saved object
        String savedJson = sharedPreferences.getString(context.getString(R.string.skynet_driver_account), null);
        SkynetDriverAppLoginBodyResponse existingData = null;

        if (savedJson != null) {
            existingData = gson.fromJson(savedJson, SkynetDriverAppLoginBodyResponse.class);
        }

        // If null, create new
        if (existingData == null) {
            existingData = new SkynetDriverAppLoginBodyResponse();
        }

        // Update only the vehicle field
        existingData.setVehicle(updatedVehicle);

        // Save updated object
        String updatedJson = gson.toJson(existingData);
        editor.putString(context.getString(R.string.skynet_driver_account), updatedJson);
        editor.commit();

        return existingData;
    }

    public static UserDTO getUserAccount() {
        Gson gson = new Gson();
        String userData = sharedPreferences.getString(context.getString(R.string.user_account), null);
        return gson.fromJson(userData, UserDTO.class);
    }

    public static SkynetDriverAppLoginBodyResponse getSkynetDriverAccount() {
        Gson gson = new Gson();
        String userData = sharedPreferences.getString(context.getString(R.string.skynet_driver_account), null);
        return gson.fromJson(userData, SkynetDriverAppLoginBodyResponse.class);
    }

    public static DriverDTO storeDriverData(DriverDTO driverDTO) {
        Gson gson = new Gson();
        String object = gson.toJson(driverDTO);
        editor.putString(context.getString(R.string.driver_data), object);
        editor.commit();

        return gson.fromJson(object, DriverDTO.class);
    }

    public static DriverDTO getDriverAccount() {
        String userData = sharedPreferences.getString(context.getString(R.string.driver_data), null);
        Gson gson = new Gson();
        return gson.fromJson(userData, DriverDTO.class);
    }

    public static DriverDTO storeDriverAutoLogin(AutoLoginDTO autoLoginDTO) {
        Gson gson = new Gson();
        String object = gson.toJson(autoLoginDTO);
        editor.putString(context.getString(R.string.driver_auto_login), object);
        editor.commit();

        Log.d("Auto_login", "init:  null" + "extras.toString()  have bookings" + object);
        return gson.fromJson(object, DriverDTO.class);
    }

    public static AutoLoginDTO getDriverAutoLogin() {
        String userData = sharedPreferences.getString(context.getString(R.string.driver_auto_login), null);
        Gson gson = new Gson();

        Log.d("Auto_login", "init:  null" + "extras.toString()  have bookings" + userData);
        return gson.fromJson(userData, AutoLoginDTO.class);

    }

    public static String getToken(Context context) {
        String userData = sharedPreferences.getString(context.getString(R.string.driver_auto_login), null);
        Gson gson = new Gson();
        AutoLoginDTO autoLoginDTO = gson.fromJson(userData, AutoLoginDTO.class);

        return context.getString(R.string.token_bearer) + " " + autoLoginDTO.getToken();
    }

    public static String getSuperAppToken(Context context) {
        return context.getString(R.string.token_bearer) + " " + getSkynetDriverAccount().getTokenResponse().getToken();
    }


    public static void signOut(Context context) {
        editor.remove(context.getString(R.string.driver_auto_login));
        editor.remove(context.getString(R.string.driver_data));
        editor.remove(context.getString(R.string.user_account));
        editor.remove(context.getString(R.string.first_fragment_label));
        editor.remove(context.getString(R.string.local_DB));
        editor.apply();

    }


    //    new notifications
    public static ArrayList<String> storeNewBooking(String oid) {
        ArrayList<String> bookings = new ArrayList<>();
        ArrayList<String> otherBooking = getNewBookings();

        if (otherBooking != null) {
            if (otherBooking.contains(oid)) {
                bookings.add(oid);
            }
            bookings.addAll(otherBooking);
        } else {
            bookings.add(oid);
        }

        Gson gson = new Gson();
        String object = gson.toJson(bookings);
        editor.putString(context.getString(R.string.new_notifications), object);
        editor.commit();
        return bookings;
    }

    public static void clearNewBookings() {
        editor.remove(context.getString(R.string.new_notifications));
        editor.commit();
    }

    public static ArrayList<String> getNewBookings() {
        String newNotifications = sharedPreferences.getString(context.getString(R.string.new_notifications), null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();

        return gson.fromJson(newNotifications, type);

    }

    public static void saveCoordinatesInPreferences(Context context, float latitude, float longitude) {
        editor.putFloat(context.getString(R.string.POINT_LATITUDE_KEY), latitude);
        editor.putFloat(context.getString(R.string.POINT_LONGITUDE_KEY), longitude);
        editor.apply();
    }

    @NotNull
    public static Location getDriverLocation(Context context) {
        float lat = sharedPreferences.getFloat(context.getString(R.string.POINT_LATITUDE_KEY), 0);
        float lng = sharedPreferences.getFloat(context.getString(R.string.POINT_LONGITUDE_KEY), 0);
        Location current = new Location("");

        current.setLatitude(lat);
        current.setLongitude(lng);
        return current;
    }

    @NotNull
    private static LocationDTO getLocation(Context context) {
        float lat = sharedPreferences.getFloat(context.getString(R.string.POINT_LATITUDE_KEY), 0);
        float lng = sharedPreferences.getFloat(context.getString(R.string.POINT_LONGITUDE_KEY), 0);
        LocationDTO current = new LocationDTO();
        final String street = sharedPreferences.getString(MainApplication.Companion.getInstance().getString(R.string.street_name), "No Street Found");
        current.setAddress(street);

        String[] coordinate = new String[]{String.valueOf(lat),
                String.valueOf(lng)};
        current.setCoordinates(coordinate);
        return current;
    }

    public static void saveStreetName(String street) {
        editor.putString(MainApplication.Companion.getInstance().getString(R.string.street_name), street);
        editor.apply();
    }

    public static void storeStandardFees(ArrayList<FeeDTO> fees) {
        Gson gson = new Gson();
        String object = gson.toJson(fees);
        editor.putString(context.getString(R.string.fees), object);
        editor.commit();
    }

    public static ArrayList<FeeDTO> getStandardFees() {
        String fees = sharedPreferences.getString(context.getString(R.string.fees), null);
        Type type = new TypeToken<ArrayList<FeeDTO>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(fees, type);
    }


    public static void storeProfile(ProfileDTO profileDTO) {
        Gson gson = new Gson();
        String object = gson.toJson(profileDTO);
        editor.putString(context.getString(R.string.profile), object);
        editor.commit();
    }

    public static ProfileDTO getProfile() {
        String profile = sharedPreferences.getString(context.getString(R.string.profile), null);
        Type type = new TypeToken<ArrayList<ProfileDTO>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(profile, type);
    }

    public static void storeIsUserBlocked(boolean status) {
        editor.putBoolean(context.getString(R.string.is_user_blocked), status);
        editor.commit();
    }

    public static boolean getIsUserBlocked() {
        return sharedPreferences.getBoolean(context.getString(R.string.is_user_blocked), false);
    }

    public static void storeIsAppSynced(boolean status) {
        editor.putBoolean(context.getString(R.string.is_app_synced), status);
        editor.commit();
    }

    public static boolean getIsAppSynced() {
        return sharedPreferences.getBoolean(context.getString(R.string.is_app_synced), true);
    }

    public static void saveLoginDate() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        editor.putString("last_login_date", today);
        editor.apply();
    }

    public static boolean isLoginToday() {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String lastLogin = sharedPreferences.getString("last_login_date", null);
        return today.equals(lastLogin) && getSkynetDriverAccount() != null;
    }

    public static void clearLoginDate() {
        editor.remove("last_login_date");
        editor.apply();
    }

    public static void storeCredentials(String username, String password) {
        try {
            assert context != null;
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();


            SharedPreferences securePrefs = EncryptedSharedPreferences.create(context, "secure_credentials", masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            securePrefs.edit()
                    .putString("cached_username", username)
                    .putString("cached_password", password)
                    .apply();


        } catch (GenericSignatureFormatError | IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public static String getCachedUsername() {

        try {
            assert context != null;
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();


            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return securePrefs.getString("cached_username", null);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCachedPassword() {

        try {
            assert context != null;
            MasterKey masterKey = new MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build();


            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            return securePrefs.getString("cached_password", null);

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clearCachedCredentials() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences securePrefs = EncryptedSharedPreferences.create(
                    context,
                    "secure_credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            securePrefs.edit().clear().apply();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

    }
}
