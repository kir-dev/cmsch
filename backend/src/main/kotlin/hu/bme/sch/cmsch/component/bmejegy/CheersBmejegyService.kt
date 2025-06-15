package hu.bme.sch.cmsch.component.bmejegy

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.form.FormService
import hu.bme.sch.cmsch.extending.BmeJegyListener
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.TimeService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
@ConditionalOnBean(BmejegyComponent::class)
@ConditionalOnProperty(name = [LEGACY_BMEJEGY_CONFIG_PROPERTY], havingValue = "false", matchIfMissing = true)
class CheersBmejegyService(
    private val bmejegyRecordRepository: BmejegyRecordRepository,
    private val formService: FormService,
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val listeners: List<BmeJegyListener>,
    private val bmejegy: BmejegyComponent,
    private val clock: TimeService,
) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val objectMapper = jacksonObjectMapper()

    fun updateUserStatuses(tickets: List<CheersTicket>) {
        val registered = bmejegyRecordRepository.findAll().associateBy { it.itemId }
        val newTickets = mutableListOf<BmejegyRecordEntity>()
        tickets.forEach { ticket ->
            if (!registered.containsKey(ticket.id)) {
                newTickets.add(BmejegyRecordEntity(
                    id = 0,
                    itemId = ticket.id,
                    item = ticket.productName ?: "",
                    fullName = ticket.buyerName ?: "",
                    status = ticket.status ?: "N/A",
                    orderKey = ticket.ticketCode ?: "",
                    email = ticket.buyerEmail ?: "",
                    qrCode = ticket.ticketId ?: "",
                    photoId = "",
                    date = ticket.purchasedAt ?: "",
                    registered = clock.getTimeInSeconds(),
                    idId = ticket.productId ?: "",
                    total = ticket.price?.toString() ?: "",
                    faculty = "",
                    matchedUserId = 0,
                    rawData = objectMapper.writeValueAsString(ticket)
                ))
                listeners.forEach { listener -> listener.onTicketRaw(ticket) }
            }
        }
        log.info("[CHEERS] Found new tickets: {}", newTickets.size)
        bmejegyRecordRepository.saveAll(newTickets)
        newTickets.forEach { ticket ->
            listeners.forEach { listener -> listener.onTicketAdded(ticket) }
        }
    }

    @Retryable(value = [ SQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    fun updateUserStatuses() {
        val unmatched = bmejegyRecordRepository.findAllByMatchedUserId(0)
        val changedTickets = mutableListOf<BmejegyRecordEntity>()
        val changedUsers = mutableListOf<UserEntity>()
        val userToTicketMapping = mutableListOf<Pair<UserEntity, BmejegyRecordEntity>>()

        if (bmejegy.completeByEmail) {
            log.info("[CHEERS] Completing by email")

            val reader = objectMapper.readerFor(object : TypeReference<MutableMap<String, String>>() {})
            val forms = formService.getSelectedForms()

            val group1 = if (bmejegy.grantGroupName1.isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName1).orElse(null) else null
            val group2 = if (bmejegy.grantGroupName2.isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName2).orElse(null) else null
            val group3 = if (bmejegy.grantGroupName3.isNotBlank())
                groupRepository.findByName(bmejegy.grantGroupName3).orElse(null) else null

            forms.forEach { form ->
                formService.getSubmissions(form).forEach { raw ->
                    val submission = reader.readValue<MutableMap<String, String>>(raw.submission)
                    val email = (submission[bmejegy.emailFieldName] ?: "").uppercase()
                    val ticket = unmatched.firstOrNull { it.email == email }
                    if (ticket != null) {
                        ticket.matchedUserId = raw.submitterUserId ?: 0
                        val user = updateUser(ticket.matchedUserId, ticket.item, group1, group2, group3)
                        if (user != null) {
                            changedUsers.add(user)
                            userToTicketMapping.add(Pair(user, ticket))
                        }
                        changedTickets.add(ticket)
                    }
                }
            }

        }

        log.info("[CHEERS] Newly matched users: {}", changedTickets.size)
        bmejegyRecordRepository.saveAll(changedTickets)

        log.info("[CHEERS] Updated users: {}", changedUsers.size)
        userRepository.saveAll(changedUsers)

        if (listeners.isNotEmpty()) {
            userToTicketMapping.forEach { mapping ->
                listeners.forEach { listener -> listener.onTicketAssigned(mapping.first, mapping.second) }
            }
        }
    }

    private fun updateUser(
        submitterUserId: Int,
        item: String,
        group1: GroupEntity?,
        group2: GroupEntity?,
        group3: GroupEntity?
    ) : UserEntity? {
        val userEntityOptional = userRepository.findById(submitterUserId)
        if (userEntityOptional.isPresent) {
            val user = userEntityOptional.orElseThrow()
            var changed = false

            if (bmejegy.forOrder1.isNotBlank() && item.contains(bmejegy.forOrder1)) {
                if (bmejegy.grantAttendee1 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged1 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group1 != null) {
                    user.group = group1
                    user.groupName = group1.name
                    changed = true
                }
            }

            if (bmejegy.forOrder2.isNotBlank() && item.contains(bmejegy.forOrder2)) {
                if (bmejegy.grantAttendee2 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged2 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group2 != null) {
                    user.group = group2
                    user.groupName = group2.name
                    changed = true
                }
            }

            if (bmejegy.forOrder3.isNotBlank() && item.contains(bmejegy.forOrder3)) {
                if (bmejegy.grantAttendee3 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.ATTENDEE
                    changed = true
                }
                if (bmejegy.grantPrivileged3 && user.role.value < RoleType.STAFF.value) {
                    user.role = RoleType.PRIVILEGED
                    changed = true
                }
                if (group3 != null) {
                    user.group = group3
                    user.groupName = group3.name
                    changed = true
                }
            }

            if (changed) {
                log.info("[CHEERS] Updating user {} to role={} group={}", user.fullName, user.role.name, user.groupName)
                return user
            }
        }
        return null
    }

}
