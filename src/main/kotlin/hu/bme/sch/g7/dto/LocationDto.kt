package hu.bme.sch.g7.dto

data class LocationDto(
        var token: String,
        var longitude: Double,
        var latitude: Double,
        var altitude: Double,
        var accuracy: Float
)