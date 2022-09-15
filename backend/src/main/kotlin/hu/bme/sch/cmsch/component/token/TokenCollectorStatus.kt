package hu.bme.sch.cmsch.component.token

enum class TokenCollectorStatus {
    SCANNED,
    ALREADY_SCANNED,
    WRONG,
    CANNOT_COLLECT,
    QR_FIGHT_LEVEL_NOT_OPEN,
    QR_FIGHT_LEVEL_LOCKED,
    QR_FIGHT_TOWER_LOCKED,
    QR_FIGHT_INTERNAL_ERROR,
    QR_TOWER_LOGGED,
    QR_TOWER_CAPTURED
}
