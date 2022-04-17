package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service

@Service
class PermissionsService {
}

class PermissionValidator(val permissionString: String = "", val validate: Function1<UserEntity, Boolean>) {
}

val PERMISSION_IMPLICIT_HAS_GROUP = PermissionValidator("")
        { user -> user.group != null }

val PERMISSION_IMPLICIT_ANYONE = PermissionValidator("")
        { _ -> true }

/// CONTROL PERMISSIONS
/// - to change component settings

val PERMISSION_CONTROL_NEWS = PermissionValidator("PERMISSION_CONTROL_NEWS")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_ACHIEVEMENTS = PermissionValidator("PERMISSION_CONTROL_ACHIEVEMENTS")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_EVENTS = PermissionValidator("PERMISSION_CONTROL_EVENTS")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_DEBTS = PermissionValidator("PERMISSION_CONTROL_DEBTS")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_RIDDLE = PermissionValidator("PERMISSION_CONTROL_RIDDLE")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_TOKEN = PermissionValidator("PERMISSION_CONTROL_TOKEN")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_EXTRAPAGES = PermissionValidator("PERMISSION_CONTROL_EXTRAPAGES")
        { user -> user.isAdmin() }

val PERMISSION_IMPORT_EXPORT = PermissionValidator("PERMISSION_IMPORT_EXPORT")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_APP = PermissionValidator("PERMISSION_CONTROL_APP")
        { user -> user.isAdmin() }

/// EDIT PERMISSIONS
/// - to use different menus

val PERMISSION_EDIT_TOKENS = PermissionValidator("PERMISSION_EDIT_TOKEN")
        { user -> user.isAdmin() }

val PERMISSION_RATE_ACHIEVEMENTS = PermissionValidator("PERMISSION_EDIT_TOKEN")
        { user -> user.isAdmin() }

// show and edit all bebts
val PERMISSION_EDIT_DEBTS = PermissionValidator("PERMISSION_EDIT_DEBTS")
        { user -> user.isAdmin() }

val PERMISSION_SHOW_DELETE_FILES = PermissionValidator("PERMISSION_SHOW_FILES")
        { user -> user.isAdmin() }

val PERMISSION_SHOW_LOCATIONS = PermissionValidator("PERMISSION_SHOW_LOCATIONS")
        { user -> user.isAdmin() }

val PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS = PermissionValidator("PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS")
        { user -> user.isAdmin() }

val PERMISSION_EXP_TRANSACTION_IMPORT = PermissionValidator("")
        { user -> user.isSuperuser() }

val PERMISSION_SELL_FOOD = PermissionValidator("PERMISSION_SELL_FOOD")
        { user -> user.isAdmin() }

val PERMISSION_SELL_MERCH = PermissionValidator("PERMISSION_SELL_MERCH")
        { user -> user.isAdmin() }

val PERMISSION_SELL_ANY_PRODUCT = PermissionValidator("PERMISSION_SELL_ANY_PRODUCT")
        { user -> user.isAdmin() }

// and show locations
val PERMISSION_TRACK_ONE_GROUP = PermissionValidator("PERMISSION_TRACK_ONE_GROUP")
        { user -> user.isAdmin() }

val PERMISSION_TRACK_EVERYBODY = PermissionValidator("PERMISSION_TRACK_EVERYBODY")
        { user -> user.isAdmin() }

val PERMISSION_SHOW_SOLD_STATS = PermissionValidator("PERMISSION_SHOW_SOLD_STATS")
        { user -> user.isAdmin() }

val PERMISSION_SHOW_LEADERBOARD = PermissionValidator("PERMISSION_SHOW_LEADERBOARD")
        { user -> user.isAdmin() }

val PERMISSION_CONTROL_LEADERBOARD = PermissionValidator("PERMISSION_CONTROL_LEADERBOARD")
        { user -> user.isAdmin() }

val PERMISSION_CREATE_ACHIEVEMENT = PermissionValidator("PERMISSION_CREATE_ACHIEVEMENT")
        { user -> user.isAdmin() }

val PERMISSION_XXXX = PermissionValidator("XXXX")
        { user -> user.isAdmin() }
