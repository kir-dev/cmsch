package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service

@Service
class ApplicationService {

    fun getUserAuthInfo(jwtUser: CmschUser, actualUser: UserEntity?): UserAuthInfoView {
        if (actualUser == null || isAuthenticationExpired(jwtUser, actualUser))
            return UserAuthInfoView(authState = AuthState.EXPIRED)

        return UserAuthInfoView(
            authState = AuthState.LOGGED_IN,
            id = actualUser.id,
            internalId = actualUser.internalId,
            role = actualUser.role,
            permissionsAsList = actualUser.permissionsAsList,
            userName = actualUser.userName,
            groupId = actualUser.groupId,
            groupName = actualUser.groupName,
        )
    }

    fun isAuthenticationExpired(jwtUser: CmschUser, actualUser: UserEntity): Boolean {
        if (jwtUser.id != actualUser.id) return true
        if (jwtUser.internalId != actualUser.internalId) return true
        if (jwtUser.role != actualUser.role) return true
        if (jwtUser.permissionsAsList != actualUser.permissionsAsList) return true
        if (jwtUser.userName != actualUser.userName) return true
        if (jwtUser.groupId != actualUser.groupId) return true
        if (jwtUser.groupName != actualUser.groupName) return true

        return false
    }

}
