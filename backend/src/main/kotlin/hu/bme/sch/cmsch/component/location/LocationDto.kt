package hu.bme.sch.cmsch.component.location

data class LocationDto(
    var token: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0,
    var accuracy: Float = 0f,
    var altitudeAccuracy: Float = 0f,
    var heading: Double = 0.0,
    var speed: Double = 0.0,
    var broadcastEnabled: Boolean = false
)
