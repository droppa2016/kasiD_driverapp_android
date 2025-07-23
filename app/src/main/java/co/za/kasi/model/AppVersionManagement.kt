package co.za.kasi.model

import com.google.gson.annotations.SerializedName

data class AppVersionResponse(
    @SerializedName("latestVersion")
    val latestVersion: LatestVersion,
    @SerializedName("latestCriticalVersionCode")
    val latestCriticalVersionCode: String
)

data class LatestVersion(
    @SerializedName("code")
    val code: String,
    @SerializedName("status")
    val status: String
)

data class AppUpdateBody(
    @SerializedName("code")
    val code: String,
    @SerializedName("version")
    val status: String
)

data class AppUpdateResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("userAccount")
    val userAccount: UserAccount,
    @SerializedName("province")
    val province: String?,
    @SerializedName("suburb")
    val suburb: String?,
    @SerializedName("county")
    val county: String?,
    @SerializedName("identityNo")
    val identityNo: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("profilePicture")
    val profilePicture: String?,
    @SerializedName("permanentRoute")
    val permanentRoute: String?,
    @SerializedName("app_version_code")
    val appVersionCode: String?,
    @SerializedName("activeAtSkynet")
    val activeAtSkyNet: Boolean
)

data class VersionCheckResponse(
    val latestVersion: String,
    val minRequiredVersion: String,
    val updateUrl: String
)

fun getVersionResponse(): VersionCheckResponse {
    return VersionCheckResponse(
        latestVersion = "1.0.0",
        minRequiredVersion = "1.0.0",
        updateUrl = "https://www.google.com"
    )
}