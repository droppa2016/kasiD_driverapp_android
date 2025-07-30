package co.za.kasi.services

import co.za.kasi.model.ManifestDTO
import co.za.kasi.model.WaybillStats
import co.za.kasi.model.superApp.a.waybillData.Waybills
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface SyncDataService {

    @GET("trip/summary/{driverIdNo}/{date}")
    suspend fun getTripSummary(
        @Header("Authorization") auth: String,
        @Path("driverIdNo") driverIdNo: String,
        @Path("date") date: String
    ): Response<List<ManifestDTO>>

    @GET("statistics/{idNumber}/stats")
   suspend fun getWaybillStats(
        @Header("Authorization") auth: String,
        @Path("idNumber") idNumber: String
    ): Response<WaybillStats>

    @GET("waybills/driver/deliveries/{driverIdNo}/{date}")
    suspend fun getDriverDeliveryWaybills(
        @Header("Authorization") auth: String,
        @Path("driverIdNo") driverIdNo: String,
        @Path("date") date: String
    ): Response<List<Waybills>>

    @GET("driver/{driverIdNo}/is/synced")
    suspend fun isDriverSynced(
        @Header("Authorization") auth: String,
        @Path("driverIdNo") driverIdNo: String
    ): Response<Boolean>

    @PUT("driver/{driverIdNo}/synced")
    suspend fun setDriverSynced(
        @Header("Authorization") auth: String,
        @Path("driverIdNo") driverIdNo: String
    ): Response<String>

}