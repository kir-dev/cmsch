package hu.bme.sch.cmsch.component.groupselection

import hu.bme.sch.cmsch.component.login.USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

@Service
@ConditionalOnBean(GroupSelectionComponent::class)
open class GroupSelectionService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun selectGroup(
        userFromSession: UserEntity,
        groupId: Int,
        request: HttpServletRequest
    ): GroupSelectionResponse {
        
        val user = userRepository.findById(userFromSession.id).orElse(null)
            ?: return GroupSelectionResponse(GroupSelectionResponseType.UNAUTHORIZED)

        val leavable = user.group?.leaveable ?: true
        if (leavable) {
            val newGroup = groupRepository.findById(groupId).orElse(null)
                    ?: return GroupSelectionResponse(GroupSelectionResponseType.INVALID_GROUP)
            user.group = newGroup
            user.groupName = newGroup.name
            userRepository.save(user)

            request.getSession(true).setAttribute(USER_ENTITY_DTO_SESSION_ATTRIBUTE_NAME, user)
            log.info("New group '${newGroup.name}' selected for user '${user.fullName}'")
            return GroupSelectionResponse(GroupSelectionResponseType.OK)
        } else {
            return GroupSelectionResponse(GroupSelectionResponseType.LEAVE_DENIED)
        }
    }


}
