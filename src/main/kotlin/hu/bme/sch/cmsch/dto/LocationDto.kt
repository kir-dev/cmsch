package hu.bme.sch.cmsch.dto

data class LocationDto(
        var token: String,
        var longitude: Double,
        var latitude: Double,
        var altitude: Double,
        var accuracy: Float
)
