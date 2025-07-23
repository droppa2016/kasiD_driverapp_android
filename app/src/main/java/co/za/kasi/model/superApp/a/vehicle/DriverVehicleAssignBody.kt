package co.za.kasi.model.superApp.a.vehicle

import co.za.kasi.model.superApp.a.waybillData.Coordinate

data class DriverVehicleAssignBody(
    var driverId : String,
    var vehicleReg : String,
    val coordinate: Coordinate
)