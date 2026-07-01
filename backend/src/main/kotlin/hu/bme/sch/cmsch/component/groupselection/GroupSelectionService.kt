package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
@ConditionalOnBean(GroupSelectionComponent::class)
class GroupSelectionService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Retryable(value = [ SQLException::class ], maxRetries = 5, delay = 500L, multiplier = 1.5)
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun selectGroup(
        user: UserEntity,
        groupId: Int
    ): GroupSelectionResponse {
        
        val leavable = user.group?.leaveable ?: true
        if (leavable) {
            val newGroup = groupRepository.findById(groupId).orElse(null)
                    ?: return GroupSelectionResponse(GroupSelectionResponseType.INVALID_GROUP)
            user.group = newGroup
            user.groupName = newGroup.name
            userRepository.save(user)

            log.info("New group '${newGroup.name}' selected for user '${user.fullName}'")
            return GroupSelectionResponse(GroupSelectionResponseType.OK)
        } else {
            return GroupSelectionResponse(GroupSelectionResponseType.LEAVE_DENIED)
        }
    }


}
