package co.za.kasi.model

import com.google.gson.annotations.SerializedName

data class ManifestResponse(
    val data: List<ManifestDTO>
)

data class ManifestDTO(
    @SerializedName("tripNo")
    val tripNo: String,
    @SerializedName("totalWaybills")
    val totalWaybills: Int,
    @SerializedName("totalParcels")
    val totalParcels: Int
)