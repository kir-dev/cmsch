package hu.bme.sch.cmsch.component.location

data class LocationDto(
        var token: String,
        var longitude: Double,
        var latitude: Double,
        var altitude: Double,
        var accuracy: Float
)
