package hu.bme.sch.cmsch.component.qrfight

data class QrFightTowerDto(
    var towerName: String = "",
    var ownerName: String = "",
    var holderName: String = "",
    var holdingFor: Int = 0,
    var totem: Boolean = false
)
