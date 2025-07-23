package co.za.kasi.model

import com.google.gson.annotations.SerializedName

data class PasswordResetBody(
    @SerializedName("driverIdNo")
    val driverIdNo: String
)

data class PasswordResetDTO(
    @SerializedName("message")
    val message: String,
    @SerializedName("userAccount")
    val userAccount: UserAccountDTO
)

data class UserAccountDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("mobileNumber")
    val mobileNumber: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("rolesAndPermissions")
    val rolesAndPermissions: List<RoleAndPermissionDTO>
)

data class RoleAndPermissionDTO(
    @SerializedName("roles")
    val roles: RoleDTO,
    @SerializedName("permissions")
    val permissions: List<String>
)

data class RoleDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("name")
    val name: String
)

data class VerifyOTPBody(
    @SerializedName("userAccountId")
    val userAccountId: String,
    @SerializedName("otp")
    val otp: String
)

data class VerifyOTPResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("confirmation")
    val confirmation: Boolean,
    @SerializedName("userAccount")
    val userAccount: UserAccountDTO
)

data class NewPasswordBody(
    @SerializedName("otp")
    val otp: String,
    @SerializedName("userAccountId")
    val userAccountId: String,
    @SerializedName("password")
    val password: String
)

data class NewPasswordResponse(
    @SerializedName("userAccount")
    val userAccount: UserAccountDTO
)