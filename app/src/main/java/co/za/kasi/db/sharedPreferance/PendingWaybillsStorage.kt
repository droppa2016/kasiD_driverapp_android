package co.za.kasi.db.sharedPreferance

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import co.za.kasi.model.superApp.a.waybillData.WaybillRequest
import co.za.kasi.model.superApp.a.waybillData.Waybills

object PendingWaybillsStorage {

    private const val PREF_NAME = "PendingWaybills"
    private const val KEY_PENDING_WAYBILLS = "PendingWaybillsList"
    private const val KEY_COMPLETED_WAYBILLS = "CompletedWaybillsList"
    private const val KEY_FAILED_WAYBILLS = "FailedWaybillsList"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveWaybill(waybillRequest: WaybillRequest) {
        val pendingList = getPendingWaybills().toMutableList()
        pendingList.add(waybillRequest)
        savePendingList(pendingList)
    }

    fun getPendingWaybills(): List<WaybillRequest> {
        val json = sharedPreferences.getString(KEY_PENDING_WAYBILLS, null)
        return if (json != null) {
            val type = object : TypeToken<List<WaybillRequest>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun clearPendingWaybills() {
        sharedPreferences.edit().remove(KEY_PENDING_WAYBILLS).apply()
    }

    private fun savePendingList(list: List<WaybillRequest>) {
        val json = Gson().toJson(list)
        sharedPreferences.edit().putString(KEY_PENDING_WAYBILLS, json).apply()
    }

    fun saveCompletedWaybill(waybill: Waybills) {
        val list = getCompletedWaybills().toMutableList()
        list.add(waybill)
        saveWaybillList(KEY_COMPLETED_WAYBILLS, list)
    }

    fun getCompletedWaybills(): List<Waybills> {
        return getWaybillList(KEY_COMPLETED_WAYBILLS)
    }

    fun saveFailedWaybill(waybill: Waybills) {
        val list = getFailedWaybills().toMutableList()
        list.add(waybill)
        saveWaybillList(KEY_FAILED_WAYBILLS, list)
    }

    fun getFailedWaybills(): List<Waybills> {
        return getWaybillList(KEY_FAILED_WAYBILLS)
    }

    private fun getWaybillList(key: String): List<Waybills> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<Waybills>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun saveWaybillList(key: String, list: List<Waybills>) {
        val json = Gson().toJson(list)
        sharedPreferences.edit().putString(key, json).apply()
    }

    fun clearAllWaybillLists() {
        savePendingList(emptyList())
        saveWaybillList(KEY_COMPLETED_WAYBILLS, emptyList())
        saveWaybillList(KEY_FAILED_WAYBILLS, emptyList())
    }

}