package co.za.kasi.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

//Home Screen
data class FinancialInformation(
    @SerializedName("driverIdNo")
    val idNumber: String,
    @SerializedName("driverName")
    val driverName: String,
    @SerializedName("totalWaybills")
    val totalWaybills: Int,
    @SerializedName("totalDeliveredWaybills")
    val totalDeliveredWaybills: Int,
    @SerializedName("totalCancelledWaybills")
    val totalCancelledWaybills: Int,
    @SerializedName("amountVAT")
    val amountVAT: Double,
    @SerializedName("amountTotal")
    val amountTotal: Double,
)

data class DailyStatistics(
    @SerializedName("driverIdNo")
    val idNumber: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("totalDeliveries")
    val totalDeliveries: Int,
    @SerializedName("completedDeliveries")
    val completedDeliveries: Int,
    @SerializedName("failedDeliveries")
    val failedDeliveries: Int,
    @SerializedName("totalCollections")
    val totalCollections: Int,
    @SerializedName("successfulCollections")
    val successfulCollections: Int,
    @SerializedName("failedCollections")
    val failedCollections: Int, )

//Rates
data class Tier(
    @SerializedName("min")
    val min: Int,
    @SerializedName("max")
    val max: Int,
    @SerializedName("price")
    val price: Double
) : Serializable

data class Branch(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("province")
    val province: String
) : Serializable

data class RateStructures(
    @SerializedName("id")
    val id: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("province")
    val province: String?, // nullable
    @SerializedName("branch")
    val branch: Branch,
    @SerializedName("tiers")
    val tiers: List<Tier>,
    @SerializedName("fixedAmount")
    val fixedAmount: Double
) : Serializable

//Reports
//Volume
data class WaybillStats(
    @SerializedName("todayTotalDeliveredWaybills")
    val todayTotalDeliveredWaybills: Int,
    @SerializedName("todayTotalFailedWaybills")
    val todayTotalFailedWaybills: Int,
    @SerializedName("todayTotalWaybills")
    val todayTotalWaybills: Int,
    @SerializedName("thisWeekTotalDeliveredWaybills")
    val thisWeekTotalDeliveredWaybills: Int,
    @SerializedName("thisWeekTotalFailedWaybills")
    val thisWeekTotalFailedWaybills: Int,
    @SerializedName("thisWeekTotalWaybills")
    val thisWeekTotalWaybills: Int,
    @SerializedName("thisMonthTotalDeliveredWaybills")
    val thisMonthTotalDeliveredWaybills: Int,
    @SerializedName("thisMonthTotalFailedWaybills")
    val thisMonthTotalFailedWaybills: Int,
    @SerializedName("thisMonthTotalWaybills")
    val thisMonthTotalWaybills: Int,
    @SerializedName("thisYearTotalDeliveredWaybills")
    val thisYearTotalDeliveredWaybills: Int,
    @SerializedName("thisYearTotalFailedWaybills")
    val thisYearTotalFailedWaybills: Int,
    @SerializedName("thisYearTotalWaybills")
    val thisYearTotalWaybills: Int,
    @SerializedName("totalWaybills")
    val totalWaybills: Int,
    @SerializedName("totalFailedWaybills")
    val totalFailedWaybills: Int,
    @SerializedName("totalDeliveredWaybills")
    val totalDeliveredWaybills: Int,
    @SerializedName("monthlyExpenses")
    val monthlyExpenses: Double,
    @SerializedName("monthlyRevenue")
    val monthlyRevenue: Double,
    @SerializedName("monthlyTotalProfit")
    val monthlyTotalProfit: Double,
) : Serializable

//Revenue
data class Trip(
    @SerializedName("tripNumber")
    val tripNumber: String,
    @SerializedName("rate")
    val rate: Double,
    @SerializedName("waybills")
    val waybills: Int,
    @SerializedName("route")
    val route: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("tripDate")
    val tripDate: String,
    @SerializedName("amount")
    val amount: Double
) : Serializable

data class TripSummaryResponse(
    @SerializedName("trips")
    val trips: List<Trip>,
    @SerializedName("revenue")
    val revenue: Double,
    @SerializedName("adminFee")
    val adminFee: Double,
    @SerializedName("expenses")
    val expenses: Double,
    @SerializedName("totalProfit")
    val totalProfit: Double
) : Serializable

//Expenses
//TODO:Later Alligator Here