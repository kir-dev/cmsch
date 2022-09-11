package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(TeamComponent::class)
class TeamService {

    fun createTeam(user: UserEntity): TeamCreationStatus {
        val groupLeavable = user.group?.leaveable ?: true
        if (groupLeavable)
            return TeamCreationStatus.ALREADY_IN_GROUP

        // TODO: impl required

        return TeamCreationStatus.OK
    }

}
