package co.za.kasi.model

import com.google.gson.annotations.SerializedName

data class DriverDeviceDTO(
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("deviceToken")
    val fcmToken: String,
)