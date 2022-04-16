package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service

@Service
class PermissionsService {
}

class PermissionValidator(val permissionString: String = "", val validate: Function1<UserEntity, Boolean>) {
}

val PERMISSION_AT_LEAST_STAFF = PermissionValidator("PERMISSION_AT_LEAST_STAFF")
        { user -> user.role.value >= RoleType.STAFF.value }

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


/// EDIT PERMISSIONS
/// - to use different menus

val PERMISSION_EDIT_TOKENS = PermissionValidator("PERMISSION_EDIT_TOKEN")
    { user -> user.isAdmin() }

