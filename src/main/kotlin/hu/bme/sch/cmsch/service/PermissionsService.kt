package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.model.UserEntity

class PermissionValidator internal constructor(
    val permissionString: String = "",
    val description: String = "",
    val validate: Function1<UserEntity, Boolean> = {
            user -> user.isAdmin() || (permissionString.isNotEmpty() && user.hasPermission(permissionString))
    }
)

sealed interface PermissionGroup {
    fun allPermissions(): List<PermissionValidator>
}

object ImplicitPermissions : PermissionGroup {

    val PERMISSION_IMPLICIT_HAS_GROUP = PermissionValidator(description = "The user has a group")
            { user -> user.group != null }

    val PERMISSION_IMPLICIT_ANYONE = PermissionValidator(description = "Everyone has this permission")
            { _ -> true }

    override fun allPermissions() = listOf(
        PERMISSION_IMPLICIT_HAS_GROUP,
        PERMISSION_IMPLICIT_ANYONE
    )
}

object ControlPermissions : PermissionGroup {

    val PERMISSION_CONTROL_NEWS = PermissionValidator(
        "NEWS_CONTROL",
        "Hírek komponens testreszabása"
    )

    val PERMISSION_CONTROL_ACHIEVEMENTS = PermissionValidator(
        "ACHIEVEMENT_CONTROL",
        "Bucketlist komponens testreszabása"
    )

    val PERMISSION_CONTROL_EVENTS = PermissionValidator(
        "EVENT_CONTROL",
        "Események komponens testreszabása"
    )

    val PERMISSION_CONTROL_DEBTS = PermissionValidator(
        "DEBT_CONTROL",
        "Debt komponens testreszabása"
    )

    val PERMISSION_CONTROL_RIDDLE = PermissionValidator(
        "RIDDLE_CONTROL",
        "Riddle komponens testreszabása"
    )

    val PERMISSION_CONTROL_TOKEN = PermissionValidator(
        "TOKEN_CONTROL",
        "Token komponens testreszabása"
    )

    val PERMISSION_CONTROL_EXTRA_PAGES = PermissionValidator(
        "EXTRAPAGES_CONTROL",
        "ExtraPage komponens testreszabása"
    )

    val PERMISSION_CONTROL_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_CONTROL",
        "LeaderBoard komponens testreszabása"
    )

    val PERMISSION_CONTROL_PROFILE = PermissionValidator(
        "PROFILE_CONTROL",
        "Profil komponens testreszabása"
    )

    val PERMISSION_IMPORT_EXPORT = PermissionValidator(
        "ALL_IMPORT_EXPORT",
        "Az összes kezelt entitás exportálása és importálása"
    )

    val PERMISSION_CONTROL_APP = PermissionValidator(
        "APP_CONTROL",
        "Az alkalazás testreszabása"
    )

    val PERMISSION_SHOW_DELETE_FILES = PermissionValidator(
        "SHOW_FILES",
        "Feltöltött fájlok megtekintése és törlése"
    )

    override fun allPermissions() = listOf(
        PERMISSION_CONTROL_NEWS,
        PERMISSION_CONTROL_ACHIEVEMENTS,
        PERMISSION_CONTROL_EVENTS,
        PERMISSION_CONTROL_DEBTS,
        PERMISSION_CONTROL_RIDDLE,
        PERMISSION_CONTROL_TOKEN,
        PERMISSION_CONTROL_EXTRA_PAGES,
        PERMISSION_CONTROL_LEADERBOARD,
        PERMISSION_CONTROL_PROFILE,
        PERMISSION_IMPORT_EXPORT,
        PERMISSION_CONTROL_APP,
        PERMISSION_SHOW_DELETE_FILES,
    )

}

object StaffPermissions : PermissionGroup {

    /// Achievement Component

    val PERMISSION_RATE_ACHIEVEMENTS = PermissionValidator(
        "ACHIEVEMENT_RATE",
        "Az achievement beadások értékelése és megtekintése"
    )

    val PERMISSION_EDIT_ACHIEVEMENTS = PermissionValidator(
        "ACHIEVEMENT_EDIT",
        "Achievement feladatok létrehozása, szerkesztése és törlése"
    )

    val PERMISSION_EDIT_ACHIEVEMENT_CATEGORIES = PermissionValidator(
        "ACHIEVEMENT_CATEGORY_EDIT",
        "Achievement kategóriák létrehozása, szerkesztése és törlése"
    )

    /// Debt Component

    val PERMISSION_EDIT_DEBTS = PermissionValidator(
        "DEBT_EDIT",
        "Összes tartozás szerkesztése és megtekintése"
    )

    val PERMISSION_EDIT_PRODUCTS = PermissionValidator(
        "DEBT_PRODUCT_EDIT",
        "Termékek létrehozása, szerkesztése és törlése"
    )

    val PERMISSION_SELL_FOOD = PermissionValidator(
        "DEBT_SELL_FOOD",
        "Étel típusú termék eladása"
    )

    val PERMISSION_SELL_MERCH = PermissionValidator(
        "DEBT_SELL_MERCH",
        "Merch típusú termék eladása"
    )

    val PERMISSION_SELL_ANY_PRODUCT = PermissionValidator(
        "DEBT_SELL_ANY_PRODUCT",
        "Bármilyen típusú termék eladása"
    )

    val PERMISSION_SHOW_SOLD_STATS = PermissionValidator(
        "DEBT_SHOW_SOLD_STATS",
        "Eladott termékek statisztikájának megtekintése"
    )

    /// Location Component

    val PERMISSION_TRACK_ONE_GROUP = PermissionValidator(
        "LOCATION_TRACK_ONE_GROUP",
        "Egy csoport követése"
    )

    val PERMISSION_TRACK_EVERYBODY = PermissionValidator(
        "LOCATION_TRACK_EVERYBODY",
        "Az összes csoport követése"
    )

    val PERMISSION_SHOW_LOCATIONS = PermissionValidator(
        "LOCATION_SHOW",
        "Az összes nyers pozíció megtekintése"
    )

    /// Riddle Component

    val PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS = PermissionValidator(
        "RIDDLE_SUBMISSIONS_SHOW_DELETE",
        ""
    )

    val PERMISSION_EDIT_RIDDLES = PermissionValidator(
        "RIDDLE_EDIT",
        "Riddle létrehozása, szerkesztése és törlése"
    )

    val PERMISSION_EDIT_RIDDLE_CATEGORIES = PermissionValidator(
        "RIDDLE_CATEGORY_EDIT",
        "Riddle kategória létrehozása, szerkesztése és törlése"
    )

    /// News Component

    val PERMISSION_EDIT_NEWS = PermissionValidator(
        "NEWS_EDIT",
        "Hírek létrehozása, szerkesztése és törlése"
    )

    /// Event Component

    val PERMISSION_EDIT_EVENTS = PermissionValidator(
        "EVENT_EDIT",
        "Események létrehozása, szerkesztése és törlése"
    )

    /// Token Component

    val PERMISSION_EDIT_TOKENS = PermissionValidator(
        "TOKEN_EDIT",
        "Tokenek létrehozása, szerkesztése és törlése"
    )

    /// User Component

    val PERMISSION_EDIT_GROUPS = PermissionValidator(
        "GROUP_EDIT",
        "Csoportok (tankörök) létrehozása, szerkesztése és törlése"
    )

    val PERMISSION_EDIT_USERS = PermissionValidator(
        "USER_EDIT",
        "Felhasználók szerkesztése és törlése"
    )

    val PERMISSION_EDIT_GUILD_MAPPINGS = PermissionValidator(
        "GUILD_MAPPING_EDIT",
        "Gárda hozzárendelések létrehozása, szerkesztése és törlése"
    )

    val PERMISSION_EDIT_GROUP_MAPPINGS = PermissionValidator(
        "GROUP_MAPPING_EDIT",
        "Csoport (tankör) hozzárendelések létrehozása, szerkesztése és törlése"
    )

    /// ExtraPages Component

    val PERMISSION_EDIT_EXTRA_PAGES = PermissionValidator(
        "EXTRAPAGE_EDIT",
        "Extra oldalak létrehozása, szerkesztése és törlése"
    )

    // LeaderBoard Component

    val PERMISSION_SHOW_LEADERBOARD = PermissionValidator(
        "LEADERBOARD_SHOW",
        "Toplista megtekintése"
    )

    override fun allPermissions() = listOf(
        PERMISSION_RATE_ACHIEVEMENTS,
        PERMISSION_EDIT_ACHIEVEMENTS,
        PERMISSION_EDIT_ACHIEVEMENT_CATEGORIES,
        PERMISSION_EDIT_DEBTS,
        PERMISSION_EDIT_PRODUCTS,
        PERMISSION_SELL_FOOD,
        PERMISSION_SELL_MERCH,
        PERMISSION_SELL_ANY_PRODUCT,
        PERMISSION_SHOW_SOLD_STATS,
        PERMISSION_TRACK_ONE_GROUP,
        PERMISSION_TRACK_EVERYBODY,
        PERMISSION_SHOW_LOCATIONS,
        PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS,
        PERMISSION_EDIT_RIDDLES,
        PERMISSION_EDIT_RIDDLE_CATEGORIES,
        PERMISSION_EDIT_NEWS,
        PERMISSION_EDIT_EVENTS,
        PERMISSION_EDIT_TOKENS,
        PERMISSION_EDIT_GROUPS,
        PERMISSION_EDIT_USERS,
        PERMISSION_EDIT_GUILD_MAPPINGS,
        PERMISSION_EDIT_GROUP_MAPPINGS,
        PERMISSION_EDIT_EXTRA_PAGES,
        PERMISSION_SHOW_LEADERBOARD
    )

}

object ExperimentalPermissions {

    val PERMISSION_EXP_TRANSACTION_IMPORT = PermissionValidator { user -> user.isSuperuser() }

}


