package co.za.kasi.model

import com.google.gson.annotations.SerializedName

data class TokenUpdateBody(
    @SerializedName("driverIdNo")
    val driverIdNo: String,
    @SerializedName("fcmToken")
    val fcmToken: String,
    @SerializedName("deviceId")
    val deviceId: String
)