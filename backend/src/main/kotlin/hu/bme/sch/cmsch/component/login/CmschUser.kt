package hu.bme.sch.cmsch.component.login

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserRepository

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

    fun isAtLeastStaff(): Boolean {
        return role == RoleType.STAFF || role == RoleType.ADMIN || role == RoleType.SUPERUSER
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

fun CmschUser.asUserEntity(userRepository: UserRepository): UserEntity {
    if (this is UserEntity)
        return this
    return userRepository.findById(this.id).orElseThrow()
}