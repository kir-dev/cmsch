package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.model.RoleType

interface CmschUser {
    val id: Int
    val internalId: String
    var role: RoleType
    var permissionsAsList: List<String>
    val userName: String
    val groupId: Int?
    val groupName: String

    fun hasPermission(permission: String): Boolean

    fun isStaff(): Boolean {
        return role == RoleType.STAFF
    }

    fun isAdmin(): Boolean {
        return role == RoleType.ADMIN || role == RoleType.SUPERUSER
    }

    fun isSuperuser(): Boolean {
        return role == RoleType.SUPERUSER
    }

    fun refresh(cmschUser: CmschUser) {
        role = cmschUser.role
        permissionsAsList = cmschUser.permissionsAsList
    }
}
