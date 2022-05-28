package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.model.RoleType

interface CmschUser {
    val id: Int
    val internalId: String
    val role: RoleType
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
}
