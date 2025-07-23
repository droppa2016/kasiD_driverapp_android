package co.za.kasi.model.superApp.a.location

import kotlinx.serialization.Serializable

@Serializable
data class CreateDriverLocation(
    val driverId: String,
    val longitude: String,
    val latitude: String,
    val batteryPercentage: String,
    val dateTime: String,
    val deviceId:String
)
