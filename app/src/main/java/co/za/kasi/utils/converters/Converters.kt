package co.za.kasi.utils.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import co.za.kasi.model.superApp.a.waybillData.Parcel
import co.za.kasi.model.superApp.a.waybillData.DeliveryConditionsDTO
import co.za.kasi.model.superApp.a.waybillData.ContactPersonDTO

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromParcelList(value: List<Parcel>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toParcelList(value: String?): List<Parcel>? {
        val listType = object : TypeToken<List<Parcel>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromDeliveryConditionsList(value: List<DeliveryConditionsDTO>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDeliveryConditionsList(value: String?): List<DeliveryConditionsDTO>? {
        val listType = object : TypeToken<List<DeliveryConditionsDTO>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromContactPersonDTO(value: ContactPersonDTO?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toContactPersonDTO(value: String?): ContactPersonDTO? {
        return gson.fromJson(value, ContactPersonDTO::class.java)
    }
}