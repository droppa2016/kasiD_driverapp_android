package co.za.kasi.services;

import java.util.List;

import co.za.kasi.model.AppUpdateResponse;
import co.za.kasi.model.AppVersionResponse;
import co.za.kasi.model.DailyStatistics;
import co.za.kasi.model.FinancialInformation;
import co.za.kasi.model.NewPasswordBody;
import co.za.kasi.model.NewPasswordResponse;
import co.za.kasi.model.PasswordResetBody;
import co.za.kasi.model.PasswordResetDTO;
import co.za.kasi.model.RateStructures;
import co.za.kasi.model.TokenUpdateBody;
import co.za.kasi.model.TripSummaryResponse;
import co.za.kasi.model.UserAccountDTO;
import co.za.kasi.model.VerifyOTPBody;
import co.za.kasi.model.VerifyOTPResponse;
import co.za.kasi.model.WaybillStats;
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriver;
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBody;
import co.za.kasi.model.superApp.a.superAppLogin.SkynetDriverAppLoginBodyResponse;
import co.za.kasi.model.superApp.a.vehicle.DriverVehicleAssignBody;
import co.za.kasi.model.superApp.a.vehicle.SkynetVehicle;
import co.za.kasi.model.superApp.a.waybillData.Coordinate;
import co.za.kasi.model.superApp.a.waybillData.DriverEventDTO;
import co.za.kasi.model.superApp.a.waybillData.Trip;
import co.za.kasi.model.superApp.a.waybillData.WaybillAttending;
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest;
import co.za.kasi.model.superApp.a.waybillData.Waybills;
import kotlin.Unit;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SuperAppHttpService {

    /*================================== Session Manager ==============================================*/
    @POST("/session/reset/password")
    Call<PasswordResetDTO> resetPassword(@Body PasswordResetBody passwordResetBody);

    @POST("/session/resend/otp/{userAccountId}")
    Call<UserAccountDTO> resendOTP(@Path("userAccountId") String userAccountId);

    @POST("/session/verify/otp")
    Call<VerifyOTPResponse> verifyOTP(@Body VerifyOTPBody verifyOTPBody);

    @POST("/session/change/password")
    Call<NewPasswordResponse> changePassword(@Body NewPasswordBody newPasswordBody);

    @POST("/session/driver/login")
    Call<SkynetDriverAppLoginBodyResponse> driverLogin(@Body SkynetDriverAppLoginBody logInDTO);

    /*================================== App Version Management ==============================================*/

    @GET("/app/version/control/latest/version")
    Call<AppVersionResponse> getLatestAppVersion();

    @PUT("/app/version/control/update/{driverIdNo}/{version}")
    Call<AppUpdateResponse> updateAppVersion(
            @Header("Authorization") String auth,
            @Path("driverIdNo") String driverIdNo,
            @Path("version") String version);


    /*================================== Push Notifications ==============================================*/

    @GET("/driver/{driverIdNo}/is/synced")
    Call<Boolean> isDriverSynced(
            @Header("Authorization") String auth,
            @Path("driverIdNo") String driverIdNo
    );

    @PUT("/driver/{driverIdNo}/synced")
    Call<String> setDriverSynced(
            @Header("Authorization") String auth,
            @Path("driverIdNo") String driverIdNo
    );

    /*================================== Push Notifications ==============================================*/

    @PUT("/driver/update/fcm/token")
    Call<SkynetDriverAppLoginBodyResponse> updateFcmToken(
            @Header("Authorization") String auth,
            @Body TokenUpdateBody tokenUpdateBody
    );

    /*================================== Driver Activity ==============================================*/

    @GET("/vehicle/fetch/{registrationNumber}")
    Call<SkynetVehicle> driverVehicleSelection(@Header("Authorization") String auth, @Path("registrationNumber") String registrationNumber);

    @POST("/driver/register/for/work")
    Call<SkynetDriver> driverAssignVehicle(@Header("Authorization") String auth, @Body DriverVehicleAssignBody driverVehicleAssignBody);

    @GET("/waybills/trips/{driver}/{date}")
    Call<Trip> getDriverTrip(@Header("Authorization") String auth, @Path("driver") String driver, @Path("date") String date);

    @POST("/waybills/attending")
    Call<DriverEventDTO> attendWaybill(@Header("Authorization") String auth, @Body WaybillAttending waybillAttending);

    @POST("/waybills/start/trip/{driverIdNo}")
    Call<Trip> startTrip(@Header("Authorization") String auth, @Path("driverIdNo") String driverIdNo, @Body Coordinate coordinate);

    @POST("/waybills/submit/update")
    Call<Waybills> submitWaybill(@Header("Authorization") String auth, @Body WaybillRequest waybillRequest);

    @GET("/waybills/filter/{driverId}/{date}/{status}")
    Call<List<Waybills>> getDriverWaybillsConditional(@Header("Authorization") String auth, @Path("driverId") String driverId, @Path("date") String date, @Path("status") String status);

    @GET("/waybills/driver/deliveries/{driverIdNo}/{date}")
    Call<List<Waybills>> getDriverDeliveryWaybills(@Header("Authorization") String auth, @Path("driverIdNo") String driverIdNo, @Path("date") String date);

    @GET("/pricing/daily/pricing/estimation/{idNumber}/{date}")
    Call<FinancialInformation> getFinancialInformation(
            @Header("Authorization") String auth,
            @Path("idNumber") String idNumber,
            @Path("date") String date
    );

    @GET("/pricing/trip/estimation/{idNumber}/{startDate}/{endDate}")
    Call<TripSummaryResponse> getTripSummary(
            @Header("Authorization") String auth,
            @Path("idNumber") String idNumber,
            @Path("startDate") String startDate,
            @Path("endDate") String endDate
    );

    @GET("/statistics/{idNumber}/stats")
    Call<WaybillStats> getWaybillStats(
            @Header("Authorization") String auth,
            @Path("idNumber") String idNumber
    );

    @GET("/rate-card/rate/by/route/{routeName}")
    Call<RateStructures> getRateStructures(
            @Header("Authorization") String auth,
            @Path("routeName") String routeName
    );

    @GET("/waybills/resend/otp/{waybill}/{date}")
    Call<Unit> resendOtp(@Header("Authorization") String auth, @Path("waybill") String waybill, @Path("date") String date);

    @GET("/statistics/daily/stats/{driverIdNo}")
    Call<DailyStatistics> getDailyStatistics(@Header("Authorization") String auth, @Path("driverIdNo") String idNumber);


}
