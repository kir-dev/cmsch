package hu.bme.sch.cmsch.component.location

enum class MapMarkerShape {
    CIRCLE,
    SQUARE,
    INFO,
    CAR,
    CROSSHAIRS,
    CAMP,
    TOWER,
    MARKER,
    HOME
}

data class MapMarker(
    var displayName: String,
    var latitude: Double,
    var longitude: Double,
    var altitude: Double,
    var accuracy: Float,
    var altitudeAccuracy: Float,
    var heading: Double,
    var speed: Double,
    var timestamp: Long,
    var markerShape: MapMarkerShape,
    var markerColor: String,
    var description: String,
)