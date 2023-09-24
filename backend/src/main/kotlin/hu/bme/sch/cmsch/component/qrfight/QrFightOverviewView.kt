package hu.bme.sch.cmsch.component.qrfight

enum class LevelStatus {
    NOT_LOGGED_IN, // user is logged out
    NOT_ENABLED, // disabled by config, manual or time interval
    NOT_UNLOCKED, // dependencies not completed
    OPEN,
    COMPLETED,
}

data class TowerView(
    val name: String = "",
    val description: String = "",
    val ownerNow: String? = null,
    val holder: String? = null,
    val holdingFor: Int? = null
)

data class TotemView(
    val name: String = "",
    val description: String,
    val owner: String? = null
)

data class QrFightLevelView(
    val name: String = "",
    val description: String = "",
    val tokenCount: Int = 0,
    val status: LevelStatus = LevelStatus.NOT_LOGGED_IN,
    val owners: String = "",
    val teams: Map<String, Int> = mapOf(),
    val towers: List<TowerView> = listOf(),
    val totems: List<TotemView> = listOf(),
)

data class QrFightOverviewView(
    val mainLevels: List<QrFightLevelView>,
    val extraLevels: List<QrFightLevelView>,
)
