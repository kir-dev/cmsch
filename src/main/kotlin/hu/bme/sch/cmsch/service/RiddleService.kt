package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service

@Service
class RiddleService {

    fun listRiddlesForRole(role: RoleType) {}

    fun getRiddleForUser(userEntity: UserEntity, riddleId: Int) {}

    fun unlockHintForUser(userEntity: UserEntity, riddleId: Int) {}

    fun submitRiddleForUser(userEntity: UserEntity, riddleId: Int, solution: String) {}

}
