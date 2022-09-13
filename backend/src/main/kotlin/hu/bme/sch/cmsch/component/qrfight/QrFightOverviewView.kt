package hu.bme.sch.cmsch.component.qrfight

enum class LevelStatus {
    NOT_LOGGED_IN, // user is logged out
    NOT_ENABLED, // disabled by config, manual or time interval
    NOT_UNLOCKED, // dependencies not comleted
    OPEN,
    COMPLETED,
}

data class TowerView(
    val name: String = "",
    val ownerNow: String? = null
)

data class QrFightLevelView(
    val name: String = "",
    val description: String = "",
    val status: LevelStatus = LevelStatus.NOT_LOGGED_IN,
    val owner: String? = null,
    val teams: Map<String, Int> = mapOf(),
    val towers: List<TowerView> = listOf()
)

data class QrFightOverviewView(
    val mainLevels: List<QrFightLevelView>,
    val extraLevels: List<QrFightLevelView>,
)
