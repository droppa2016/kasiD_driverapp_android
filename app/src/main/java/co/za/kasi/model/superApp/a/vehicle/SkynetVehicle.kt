package co.za.kasi.model.superApp.a.vehicle

import co.za.kasi.model.DataObject

class SkynetVehicle : DataObject {
    var createdAt: String? = null
    var updatedAt: String? = null
    var register_number: String? = null
    var registration_number: String? = null
    var year: String? = null
    var model: String? = null
    var make: String? = null
    var vin_number: String? = null
    var license_expiry_date: String? = null

    constructor(
        createdAt: String?,
        updatedAt: String?,
        register_number: String?,
        registration_number: String?,
        year: String?,
        model: String?,
        make: String?,
        vin_number: String?,
        license_expiry_date: String?
    ) {
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.register_number = register_number
        this.registration_number = registration_number
        this.year = year
        this.model = model
        this.make = make
        this.vin_number = vin_number
        this.license_expiry_date = license_expiry_date
    }

    constructor()
}
