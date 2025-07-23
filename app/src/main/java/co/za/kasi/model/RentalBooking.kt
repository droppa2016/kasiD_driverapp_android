package co.za.kasi.model

import java.io.Serializable

class RentalBooking : DataObject(), Serializable {
    var from: LocationDTO? = null
    var branchOid: String? = null
    var `in`: String? = null

    var out: String? = null
    var rateUnit: String? = null
    var category: String? = null
    var assistance: Int? = null
    var includedDistance: Int? = null
    var comments: String? = null
    var customerOid: String? = null
    var total = 0.0
    var platform: String? = null
}
