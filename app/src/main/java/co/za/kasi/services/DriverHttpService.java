package co.za.kasi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.za.kasi.model.Avatar;
import co.za.kasi.model.Booking;
import co.za.kasi.model.BookingFlightInfoDTO;
import co.za.kasi.model.BucketBooking;
import co.za.kasi.model.CompleteBookingDTO;
import co.za.kasi.model.Contact;
import co.za.kasi.model.DeliveryDetail;
import co.za.kasi.model.DriverAccountDTO;
import co.za.kasi.model.DriverLocationTracking;
import co.za.kasi.model.DriverRegistrationDTO;
import co.za.kasi.model.DriverTracking;
import co.za.kasi.model.ExpressElement;
import co.za.kasi.model.FeeDTO;
import co.za.kasi.model.LicenceImage;
import co.za.kasi.model.LogInDTO;
import co.za.kasi.model.NewPasswordDTO;
import co.za.kasi.model.OtpVerificationDTO;
import co.za.kasi.model.PointOfInterest;
import co.za.kasi.model.ProfileDTO;
import co.za.kasi.model.PushNotificationDTO;
import co.za.kasi.model.RentalDTO;
import co.za.kasi.model.RequestBodyDTO;
import co.za.kasi.model.ResendOtpDTO;
import co.za.kasi.model.ResetPasswordDTO;
import co.za.kasi.model.SignatureUpdate;
import co.za.kasi.model.UpdateRentalDistanceDTO;
import co.za.kasi.model.VehicleType;
import co.za.kasi.model.accountDTO.DriverDTO;
import co.za.kasi.model.accountDTO.UserDTO;
import co.za.kasi.model.driverOwner.Billing;
import co.za.kasi.model.driverOwner.ParcelSammary;
import co.za.kasi.model.driverOwner.Rates;
import co.za.kasi.model.systemDTO.WhatsappAccountsDTO;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface DriverHttpService {



    /*================================== Authentication Services ==============================================*/

    @POST("sessions/driver/login")
    Call<UserDTO> accessAccount(@Body LogInDTO logInDTO);

    @POST("parties/drivers/avatar")
    Call<DriverRegistrationDTO> registerDriver(@Body DriverRegistrationDTO driverRegistrationDTO);

    @POST("parties/drivers/update/license")
    Call<DriverRegistrationDTO> updateDriver(@Body DriverRegistrationDTO driverRegistrationDTO);

    @GET("parties/drivers/filter/{identityNumber}")
    Call<DriverDTO> verifyAccount(@Path("identityNumber") String identityNumbeer);

    @GET("parties/drivers/filter/{identityNumber}")
    Call<ResponseBody> verifyAccountTest(@Path("identityNumber") String identityNumbeer);

    @POST("parties/drivers/updates/location")
    Call<DriverLocationTracking> updateLocation(@Body DriverLocationTracking locationTracking);

    @GET("parties/drivers/point/of/interest")
    Call<List<PointOfInterest>> getPointOfInterest(@Header("Authorization") String auth);

    @POST("parties/drivers/updates/phone")
    Call<DriverLocationTracking> updateOneSignalID(@Body DriverLocationTracking locationTracking);

    /*========================================================================================================*/
    /*====================================== Bookings Services ===========================================*/

    @GET("drops/bookings/reserved/{driverOid}")
    Call<ArrayList<Booking>> getDriverReservedBookings(@Header("Authorization") String auth, @Path("driverOid") String driverOID);

    @GET("bookings/buckets/status/{status}/driver/{driverOid}")
    Call<ArrayList<BucketBooking>> getDriverReservedBuckets(@Header("Authorization") String auth, @Path("driverOid") String driverOid, @Path("status") String status);

    @GET("drops/bookings/incomplete/{vehicleType}/{status}/{province}")
    Call<ArrayList<Booking>> getAwaitingbookings(@Header("Authorization") String auth, @Path("vehicleType") String vehicleType, @Path("status") String status, @Path("province") String province);

    @GET("bookings/buckets/available/{vehicleType}/{driverOid}/{province}")
    Call<ArrayList<BucketBooking>> getAwaitingbuckets(@Header("Authorization") String auth,  @Path("vehicleType") String vehicleType, @Path("driverOid") String driverOid, @Path("province") String province);

    @GET("drops/bookings/reserved/{driverOID}")
    Call<ArrayList<Booking>> getReservedBookings(@Header("Authorization") String auth, @Path("driverOID") String driverOID);

    @POST("drops/check/bookings/availability")
    Call<ArrayList<Booking>> getNewBookings(@Header("Authorization") String auth, @Body ArrayList<String> bookings);

    @GET("bookings/buckets/status/{status}/driver/{driverOid}")
    Call<ArrayList<BucketBooking>> getReservedBuckets(@Header("Authorization") String auth, @Path("status") String status ,@Path("driverOid") String driverOid);

    @POST("bookings/buckets/recreate/{bookingOid}")
    Call<ResponseBody> cancelBucketBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid );

    @POST("drops/recreate/booking/{bookingOid}")
    Call<ResponseBody> cancelSingleBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid );

    @POST("drops/book/change/status")
    Call<ResponseBody> changeSingleStatus(@Header("Authorization") String auth, @Body HashMap requestBody);



    @POST("drops/book/change/status")
    Call<ResponseBody> changeSingleBookingStatus(@Header("Authorization") String auth, @Body HashMap requestBody);

    /*========================================================================================================*/
    /*====================================== Athentication Services ===========================================*/

    @GET("admin/whatsapp/driver/number")
    Call<WhatsappAccountsDTO> getActiveWhatsappAccounts(@Header("Authorization") String auth);

    @POST("sessions/send/otp/{place}/{bookingOid}/{mobileNo}")
    Call<ResponseBody> requestVerificationCode(@Header("Authorization") String auth,@Path("place") String place,@Path("bookingOid") String bookingOid,@Path("mobileNo") String mobileNo);
    @POST("sessions/confirm/verification/{placeAt}/{booingOid}/{code}")
    Call<ResponseBody> verifyClientCoder(@Header("Authorization") String auth,@Path("place") String place,@Path("bookingOid") String bookingOid,@Path("code") String code);

    /*========================================================================================================*/


    @POST("parties/drivers/licence/img")
    Call<LicenceImage> upload(@Body LicenceImage image);

    @POST("bookings/bucket/attending/{bookingOid}")
    Call<BucketBooking> intransictBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid);
    @POST("bookings/bucket/intransit/{bookingOid}")
    Call<BucketBooking> collectEllement(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid);


    @POST("bookings/pickup/bucket/{bucketOid}/booking/{bookingOid}")
    Call<BucketBooking> updateCollectedBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid, @Path("bucketOid") String bucketOid);

    @POST("bookings/complete/bucket/{bucketOid}/element/{bookingOid}")
    Call<BucketBooking> updateCompleteElement(@Header("Authorization") String auth, @Path("bucketOid") String bucketOid, @Path("bookingOid") String bookingOidv);

    @POST("bookings/bucket/complete/{bookingOid}")
    Call<BucketBooking> updateCompletedBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid);

    @GET("bookings/bucket/{oid}")
    Call<BucketBooking> getBucket(@Header("Authorization") String auth, @Path("oid") String bookingOid); //@Header("Authorization") String auth,

    @GET("drops/booking/by/drops/{dropOid}")
    Call<Booking> getBookingByDrop(@Header("Authorization") String auth, @Path("dropOid") String dropOid);

    @GET("bookings/deliver/details/{oid}")
    Call<DeliveryDetail> getDeliveryDetails(@Header("Authorization") String auth, @Path("oid") String dropOid);

    @GET("drops/booking/retrieve/{oid}")
    Call<Booking> getBooking(@Header("Authorization") String auth, @Path("oid") String oid);

    @GET("bookings/waybill/{waybill}")
    Call<Booking> getBookingTrackingWaybil(@Path("waybill") String trackNo); //@Header("Authorization") String auth,

    @POST("bookings/buckets/decline/booking/{bookingId}")
    Call<ResponseBody> declineElement(@Header("Authorization") String auth, @Path("bookingId") String bookingOid);

    @POST("bookings/buckets/accept/{bookingOid}/{driverOid}")
    Call<ResponseBody> acceptBucketBooking(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid, @Path("driverOid") String driverOid);

    @POST("bookings/buckets/accept/booking/{bookingId}/{driverId}")
    Call<Booking> acceptElement(@Header("Authorization") String auth, @Path("bookingId") String bookingId, @Path("driverId") String driverId);

    @POST("bookings/buckets/decline/sameday/{bucketOid}/booking")
    Call<ResponseBody> cancelSamedaySDX(@Header("Authorization") String auth, @Path("bucketOid") String bucketOid);

    @GET("vehicles/vehicleTypeRate/all")
    Call<List<VehicleType>> vehicleTypeRates();

    @POST("parties/persons/signatures")
    Call<List<Contact>> putSignature(@Body SignatureUpdate signature);

    @POST("sessions/register/new/driver/mobile")
    Call<DriverAccountDTO> registerMobileNumber(@Body DriverAccountDTO driverAccountDTO);

    @POST("sessions/verify/otp/{personOid}/{code}")
    Call<ResponseBody> verifyResetCode(@Path("personOid")String personOid,@Path("code")String code);

    @POST("parties/persons/avatars")
    Call<ResponseBody> updateProfile(@Header("Authorization") String auth,@Body HashMap map);

    @POST("sessions/reset/pwd/mobile/{username}")
    Call<ResetPasswordDTO> resetPassword(@Path("username") String username);

    @POST("sessions/mobile/confirmations/{mobile}/{code}")
    Call<OtpVerificationDTO> otpVerification(@Path("mobile") String mobile, @Path("code") String code);

    @POST("sessions/resend/otp/{mobile}")
    Call<ResendOtpDTO> resendOtp(@Path("mobile") String mobile);

    @POST("drops/complete/booking/signature/{bookingRef}")
    Call<ResponseBody> sendPOD(@Header("Authorization") String auth, @Path("bookingRef") String bookingRef);

    @Multipart
    @POST("bookings/uploads/invoice")
    Call<BucketBooking> uploadImageWithDescription(@Header("Authorization") String auth, @Part MultipartBody.Part image, @Part("bookingOid") RequestBody bookingOid, @Part("bucketOid") RequestBody bucketOid, @Part("returnItems") RequestBody returnItems, String returnComment);

    @Multipart
    @POST("bookings/uploads/invoice")
    Call<BucketBooking> uploadImage(@Header("Authorization") String auth, @Part MultipartBody.Part image, @Part("bookingOid") RequestBody bookingOid, @Part("bucketOid") RequestBody bucketOid, @Part("returnItems") RequestBody returnItems, @Part("returnComment") RequestBody returnComment);

    @Multipart
    @POST("bookings/uploads/pod")
    Call<ResponseBody> uploadPODImage(@Header("Authorization") String auth, @Part MultipartBody.Part image, @Part("driverOid") RequestBody bookingOid);


    @POST("parties/driver/delete/duplicate/{driverOid}/onesignal/{oneSignalId}")
    Call<ResponseBody> removeDuplicate(@Header("Authorization") String auth, @Path("driverOid") String driverOid, @Path("oneSignalId") String oneSignalID);

    @POST("bookings/express/element/start")
    Call<BucketBooking> startExpressElement(@Header("Authorization") String auth, @Body ExpressElement element);

    @POST("bookings/element/collected/{bookingID}/{bucketOid}")
    Call<BucketBooking> elementCollected(@Header("Authorization") String auth, @Path("bookingID") String bookingID, @Path("bucketOid") String bucketOid);

    @POST("bookings/element/collected/{bookingID}")
    Call<Booking> elementCollected(@Header("Authorization") String auth, @Path("bookingID") String bookingID);

    @POST("bookings/element/completed/{bookingID}")
    Call<Booking> elementCompleted(@Header("Authorization") String auth, @Path("bookingID") String bookingID);

    @POST("bookings/air/element/completed")
    Call<Booking> airLineElementCompleted(@Header("Authorization") String auth, @Body BookingFlightInfoDTO flightInfoDTO);


    @GET("bookings/driver/completed/{driverOid}")
    Call<CompleteBookingDTO> getBookings(@Header("Authorization") String auth, @Path("driverOid") String driverOid);

    @POST("bookings/started/{bookingOid}")
    Call<ResponseBody> sendPickUpNotificationMsg(@Header("Authorization") String auth, @Path("bookingOid") String bookingOid, @Body PushNotificationDTO pushNotificationDTO);

    @POST("bookings/buckets/complete/admin/{bucketOid}")
    Call<BucketBooking> completeBucketCompletely(@Header("Authorization") String auth, @Path("bucketOid") String bucketOid);

    //NIC WILL PROVIDE ME WITH A SERVICE HERE

    @GET("rentals/awaiting/{driverOid}")
    Call<List<RentalDTO>> getRentals(@Header("Authorization") String auth, @Path("driverOid") String driverOid);

    @GET("rentals/filters/{oid}")
    Call<RentalDTO> getRental(@Header("Authorization") String auth, @Path("oid") String oid);

    @GET("parties/drivers/mobile/filter/{mobile}")
    Call<DriverDTO> getDriver(@Header("Authorization") String auth, @Path("mobile") String mobile);

    @GET("parties/persons/avatars/driver/{driverOid}")
    Call<Avatar> getAvatar(@Header("Authorization") String auth, @Path("driverOid") String driverOid);

    @GET("drops/droppa/driver/fee")
    Call<ArrayList<FeeDTO>> getFees(@Header("Authorization") String auth);

    @GET("owner/driver/vehicle/{vehicleOid}/rate")
    Call<ResponseBody> getVehicleRate(@Header("Authorization") String auth, @Path("vehicleOid") String vehicleOid);

    @POST("owner/driver/company/transaction/{vehicleOid}")
    Call<ResponseBody> getCompanytransactions(@Header("Authorization") String auth, @Body HashMap requestBodyDTO, @Path("vehicleOid") String driverOid);

    @POST("owner/driver/financial/transactions")
    Call<ResponseBody> getFinancialTransactions(@Header("Authorization") String auth, @Body HashMap requestBodyDTO);

    @GET("parties/persons/avatars/{ownerID}")
    Call<ProfileDTO> getProfile(@Header("Authorization") String auth, @Path("ownerID") String ownerID);

    @POST("sessions/update/password")
    Call<ResponseBody> enterNewPassword(@Body NewPasswordDTO newPasswordDTO);

    @GET("owner/vehicle/deliveries/{vehicleId}/billing")
    Call<ParcelSammary> getParcelSummery(@Header("Authorization") String auth, @Path("vehicleId") String vehicleId);


    @GET("owner/routes/{contractId}")
    Call<List<Rates>> getDriverOwnerRates(@Header("Authorization") String auth, @Path("contractId") String contractId);

    @GET("owner/vehicle/{vehicleID}/billing")
    Call<Billing> getMTDReport(@Header("Authorization") String auth, @Path("vehicleID") String vehicleID);

    @POST("owner/driver/filter/transaction/summary")
    Call<ResponseBody> getDrivertRansactionsSummary(@Header("Authorization") String auth, @Body HashMap reqBoby);

    @POST("owner/driver/penalty/driver")
    Call<ResponseBody> getDriverPenalties(@Header("Authorization") String auth, @Body RequestBodyDTO requestBodyDTO);

    @POST("rentals/{rentalOid}/accept/{driverOid}")
    Call<RentalDTO> acceptRental(@Header("Authorization") String auth, @Path("rentalOid") String rentalOid, @Path("driverOid") String driverOid);

    @GET("rentals/reserved/{driverOid}")
    Call<List<RentalDTO>> getReservedRental(@Header("Authorization") String auth, @Path("driverOid") String driverOid);

    @POST("bookings/failed/sdx/{trackNo}")
    Call<ResponseBody> failedSDX(@Header("Authorization") String auth, @Path("trackNo") String trackNo, @Body HashMap body);

    @POST("rentals/{rentalOid}/initial/distance")
    Call<UpdateRentalDistanceDTO> sendInitialDistance(@Header("Authorization") String auth, @Path("rentalOid") String rentalOid, @Body UpdateRentalDistanceDTO distanceDTO);

    @POST("rentals/{rentalOid}/final/distance")
    Call<UpdateRentalDistanceDTO> sendFinalDistance(@Header("Authorization") String auth, @Path("rentalOid") String rentalOid, @Body UpdateRentalDistanceDTO distanceDTO);

    @GET("drops/booking/{trackNo}")
    Call<Booking> getRecievedBooking(@Header("Authorization") String auth, @Path("trackNo") String trackNo);

    @POST("owner/driver/assign/driver/vehicle")
    Call<ResponseBody> secureVehicle(@Header("Authorization") String auth, @Body HashMap distanceDTO);

    /*==============================================================================================================*/
    /*=============================================Firebase endpoints====================================*/

    @PUT("driverTracking/{mobile}.json")
    Call<DriverTracking> updateDriverLocation(@Body DriverTracking tracking, @Path("mobile") String mobile);
    /*==============================================================================================================*/
    /*==============================================================================================================*/

}
