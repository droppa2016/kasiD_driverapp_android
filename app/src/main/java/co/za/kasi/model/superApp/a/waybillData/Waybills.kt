package co.za.kasi.model.superApp.a.waybillData

import android.os.Build
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import co.za.kasi.model.DataObject
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.za.kasi.utils.converters.Converters
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Entity(tableName = "waybills")
data class Waybills(
    @PrimaryKey
    @NonNull
    val number: String,
    @TypeConverters(Converters::class)
    val sender: ContactPersonDTO?,
    @TypeConverters(Converters::class)
    val consignee: ContactPersonDTO?,
    val serviceType: String?,
    val specialInstructions: String?,
    val date: String?,
    val accountNumber: String?,
    val orderNumber: String?,
    val clientReference: String?,
    val statusDescription: String?,
    val deliveryBranch: String?,
    val captureDate: String?,
    val deliveryDate: String?,
    val options: String?,
    @TypeConverters(Converters::class)
    val deliveryConditions: List<DeliveryConditionsDTO>?,
    @TypeConverters(Converters::class)
    val parcels: List<Parcel>?,
    val consolidated : Boolean,
    val tripNumber : String?,
    val consolidationId : String
) : Serializable {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCreatedAtDate(): LocalDateTime? {
        return try {
            LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            null
        }
    }
}

data class Trip(
    val id: String,
    val createdAt: String,
    val updatedAt: String,
    val tripDate: String,
    val driverIdNo: String,
    val waybills: List<Waybills>
) : DataObject(){
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCreatedAtDate(): LocalDateTime? {
        return try {
            LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            null
        }
    }
}

data class Parcel(
    val number: String?,
    val length: Double,
    val breadth: Double,
    val height: Double,
    val mass: Double,
    val description: String?,
    val dLNumber: String?,
    val deliveryException: Int,
    val bay: String?,
    val sealNumber: String?,
    val contents: String?,
    val itemsOnInvoice: String?,
    val rcvDateTime: String?,
    val parcelOtherReferences: String?,
    val isOnHold: Boolean,
    val isDeleted: Boolean,
    val comments: List<ParcelCommentDTO>?
) : Serializable

data class ParcelCommentDTO(
    val commentDate: String?,
    val branchCode: String?,
    val comment: String?
): Serializable
data class ContactPersonDTO(
    val name: String?,
    val contact: String?,
    val telephoneNumber: String?,
    val addressLine1: String?,
    val addressLine2: String?,
    val addressLine3: String?,
    val town: String?,
    val postalCode: String?,
    val emailAddress: String?,
    val cellNumber: String?,
    val secondCellNumber: String?
): Serializable

data class DeliveryConditionsDTO(
    val code: String?,
    val continueOnFailure: Boolean,
    val value: String?
): Serializable

data class DriverEventDTO(
    val waybillNumber: String?,
    val description: String?,
    val tripId: String?,
): Serializable

data class WaybillAttending(
    val waybillNumber : List<String>?,
    val driverId: String?,
    val deviceId: String?,
    val coordinate: Coordinate
): Serializable


data class WaybillRequest(
    val driverId: String,
    val waybillNumber: String,
    val status: String,
    val deliveryException: String,
    val scanParcelList: List<ScanParcel>,
    val deliveryConditionSubmitList: List<DeliveryCondition>,
    val coordinate: Coordinate,
    val signature: String,
    val deviceId: String,
    val recipientName: String
)


data class ScanParcel(
    val parcel_number: String,
    var scanned: Boolean
)

data class DeliveryCondition(
    val code: String,
    var value: String,
    var type: String,
    var images: List<String>,
    var captureDateTime: String
)

data class Coordinate(
    val latitude: Double,
    val longitude: Double
)

sealed class WaybillListItem {
    data class Header(val waybillNumber: String) : WaybillListItem()
    data class ParcelItem(val waybillNumber: String, val scanParcel: ScanParcel) : WaybillListItem()
}