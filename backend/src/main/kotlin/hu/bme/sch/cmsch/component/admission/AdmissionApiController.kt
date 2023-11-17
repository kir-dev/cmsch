package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.bmejegy.BmejegyRecordEntity
import hu.bme.sch.cmsch.component.bmejegy.BmejegyService
import hu.bme.sch.cmsch.component.form.ResponseRepository
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.ResolveRequest
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/admin/admission")
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
@ConditionalOnBean(AdmissionComponent::class)
class AdmissionApiController(
    private val userService: UserService,
    private val admissionComponent: AdmissionComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val bmejegyService: Optional<BmejegyService>,
    private val auditLogService: AuditLogService,
    private val responseRepository: Optional<ResponseRepository>,
    private val admissionService: AdmissionService,
    private val transactionManager: PlatformTransactionManager
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun admission(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_VALIDATE_ADMISSION.validate(user).not()) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            model.addAttribute("user", user)

            auditLogService.admin403(user, admissionComponent.component,
                "GET /admission", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            return "admin403"
        }

        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        model.addAttribute("resolveUrl", "/resolve")
        return "admission"
    }

    @GetMapping("/ticket")
    fun ticketAdmission(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_VALIDATE_ADMISSION.validate(user).not()) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            model.addAttribute("user", user)

            auditLogService.admin403(user, admissionComponent.component,
                "GET /admission/ticket", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            return "admin403"
        }

        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        model.addAttribute("resolveUrl", "/ticket-resolve")
        return "admission"
    }

    @GetMapping("/form/{formId}")
    fun admissionForm(model: Model, auth: Authentication, @PathVariable formId: Int): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_VALIDATE_ADMISSION.validate(user).not()) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            model.addAttribute("user", user)

            auditLogService.admin403(user, admissionComponent.component,
                "GET /admission/form/${formId}", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            return "admin403"
        }

        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        model.addAttribute("resolveUrl", "/resolve/${formId}")
        return "admission"
    }

    @ResponseBody
    @PostMapping("/resolve")
    fun resolve(@RequestBody resolve: ResolveRequest, auth: Authentication): AdmissionResponse {
        log.info("Resolving admission for: ${resolve.cmschId}")
        val admissionResponse = transactionManager.transaction(readOnly = true) {
            if (resolve.cmschId.startsWith(startupPropertyConfig.profileQrPrefix)) {
                userService.searchByCmschId(resolve.cmschId)
                    .map { mapUserByDetails(it) }
                    .orElse(
                        AdmissionResponse(
                            "NOT FOUND BY CMSCHID", "NOT FOUND BY CMSCH",
                            RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                        )
                    )
            } else if (bmejegyService.isPresent) {
                val ticket = bmejegyService.flatMap { it.findUserByVoucher(resolve.cmschId) }
                if (ticket.isEmpty) {
                    AdmissionResponse(
                        "NOT FOUND BY BMEJEGY", "NOT FOUND BY BMEJEGY",
                        RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                    )

                } else if (ticket.orElseThrow().matchedUserId > 0) {
                    mapTicketUser(userService.getByUserId(ticket.orElseThrow().matchedUserId), ticket.orElseThrow())

                } else {
                    mapTicket(ticket.orElseThrow())
                }
            } else {
                AdmissionResponse(
                    "NOT FOUND AT ALL", "NOT FOUND AT ALL",
                    RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                )
            }
        }
        admissionService.logEntryAttempt(admissionResponse, auth.getUser(), resolve.cmschId)
        return admissionResponse
    }

    @ResponseBody
    @PostMapping("/ticket-resolve")
    fun ticketResolve(@RequestBody resolve: ResolveRequest, auth: Authentication): AdmissionResponse {
        log.info("Resolving ticket admission for: ${resolve.cmschId}")
        var additionalInfo = ""

        val admissionResponse = transactionManager.transaction(readOnly = true) {
            if (bmejegyService.isPresent && admissionComponent.ticketAllowBmejegy.isValueTrue()) {
                val ticket = bmejegyService.flatMap { it.findUserByVoucher(resolve.cmschId) }
                if (ticket.isEmpty) {
                    AdmissionResponse(
                        "NOT FOUND BY BMEJEGY", "NOT FOUND BY BMEJEGY",
                        RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                    )

                } else if (ticket.orElseThrow().matchedUserId > 0) {
                    additionalInfo = ticket.orElseThrow().item
                    mapTicketUser(
                        user = userService.getByUserId(ticket.orElseThrow().matchedUserId),
                        ticket = ticket.orElseThrow()
                    )

                } else {
                    additionalInfo = ticket.orElseThrow().item
                    mapTicket(ticket.orElseThrow())
                }
            } else {
                val ticket = admissionService.getTicketByQr(resolve.cmschId)
                if (ticket == null) {
                    AdmissionResponse(
                        "NOT FOUND BY TICKET", "NOT FOUND BY TICKET",
                        RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                    )

                } else {
                    mapTicketUser(ticket = ticket)
                }
            }
        }
        admissionService.logEntryAttempt(admissionResponse, auth.getUser(), resolve.cmschId)
        if (admissionComponent.ticketShowEntryCount.isValueTrue()) {
            val count = admissionService.countEntries(resolve.cmschId)
            admissionResponse.groupName = if (count > 1) "NEM ELSŐ!!! (${count}.)" else "($count)"
        }
        admissionResponse.groupName += " $additionalInfo"

        return admissionResponse
    }

    @ResponseBody
    @PostMapping("/resolve/{formId}")
    fun resolveForm(
        @RequestBody resolve: ResolveRequest,
        @PathVariable formId: Int,
        auth: Authentication
    ): AdmissionResponse {
        log.info("Resolving admission for: ${resolve.cmschId} by form: ${formId}")
        val admissionResponse = transactionManager.transaction(readOnly = true) {
            if (resolve.cmschId.startsWith(startupPropertyConfig.profileQrPrefix)) {
                userService.searchByCmschId(resolve.cmschId)
                    .map { mapUserByForm(it, formId) }
                    .orElse(
                        AdmissionResponse(
                            groupName = "NOT FOUND BY CMSCHID", userName = "NOT FOUND BY CMSCH",
                            role = RoleType.GUEST, entryRole = EntryRole.CANNOT_ATTEND,
                            accessGranted = false, formId = formId
                        )
                    )
            } else {
                AdmissionResponse(
                    groupName = "NOT FOUND AT ALL", userName = "NOT FOUND AT ALL",
                    role = RoleType.GUEST, entryRole = EntryRole.CANNOT_ATTEND,
                    accessGranted = false, formId = formId
                )
            }
        }
        admissionService.logEntryAttempt(admissionResponse, auth.getUser(), resolve.cmschId)
        return admissionResponse
    }

    // TODO: This implementation is created for GÓLYABÁL 2022. Generalize it in the future if needed.
    private fun mapTicket(ticket: BmejegyRecordEntity): AdmissionResponse {
        return AdmissionResponse("NOT SYNCED : ${ticket.faculty.ifBlank { "KÜLSŐS" }}",
            "${ticket.fullName} @ ${ticket.photoId}",
            RoleType.BASIC,
            EntryRole.USER,
            true)
    }

    private fun mapTicketUser(user: UserEntity, ticket: BmejegyRecordEntity): AdmissionResponse {
        if (isBanned(user.cmschId, user.groupName))
            return AdmissionResponse(user.groupName, user.fullName, user.role, EntryRole.BANNED, false)

        val grants = mutableSetOf<EntryRole>()

        addGrantsByRole(user, grants)
        addGrantsByCmschId(user.cmschId.lowercase(), grants)
        addGrantsByGroup(user.groupName.lowercase(), grants)

        val grant = grants.maxByOrNull { it.value } ?: EntryRole.CANNOT_ATTEND
        log.info("Admission (with voucher) is ${if (grant.canAttend) "OK" else "DENIED"} for user '${user.fullName}' with group '${user.groupName}' as $grant")
        return AdmissionResponse(
            "${user.groupName} ${ticket.faculty.ifBlank { "KÜLSŐS" }}",
            "${ticket.fullName} @ ${ticket.photoId}",
            user.role, grant, grant.canAttend)
    }

    private fun mapTicketUser(ticket: TicketEntity): AdmissionResponse {
        log.info("Admission (with voucher ticket) is GRANTED for user '${ticket.owner}' with email '${ticket.email}' as ${ticket.grantType.name}")
        return AdmissionResponse(
            "JEGY",
            "${ticket.owner} @ ${ticket.email}",
            RoleType.BASIC, ticket.grantType, true)
    }

    private fun mapUserByDetails(user: UserEntity): AdmissionResponse {
        if (isBanned(user.cmschId, user.groupName))
            return AdmissionResponse(
                groupName = user.groupName,
                userName = user.fullName,
                role = user.role,
                entryRole = EntryRole.BANNED,
                accessGranted = false,
                userEntity = user
            )

        val grants = mutableSetOf<EntryRole>()

        addGrantsByRole(user, grants)
        addGrantsByCmschId(user.cmschId.lowercase(), grants)
        addGrantsByGroup(user.groupName.lowercase(), grants)

        val grant = grants.maxByOrNull { it.value } ?: EntryRole.CANNOT_ATTEND
        log.info("Admission is ${if (grant.canAttend) "OK" else "DENIED"} for user '${user.fullName}' with group '${user.groupName}' as $grant")
        return AdmissionResponse(
            groupName = user.groupName,
            userName = user.fullName,
            role = user.role,
            entryRole = grant,
            accessGranted = grant.canAttend,
            userEntity = user
        )
    }

    private fun mapUserByForm(user: UserEntity, formId: Int): AdmissionResponse {
        if (isBanned(user.cmschId, user.groupName))
            return AdmissionResponse(
                groupName = user.groupName,
                userName = user.fullName,
                role = user.role,
                entryRole = EntryRole.BANNED,
                accessGranted = false,
                userEntity = user,
                formId = formId,
            )

        if (responseRepository.isEmpty) {
            return AdmissionResponse(
                groupName = "FORMS NOT ENABLED",
                userName = "FORMS NOT ENABLED",
                role = RoleType.GUEST,
                entryRole = EntryRole.CANNOT_ATTEND,
                accessGranted = false,
                userEntity = user,
                formId = formId,
            )
        }

        val response = responseRepository.orElseThrow().findByFormIdAndSubmitterUserId(formId, user.id)
        if (response.isEmpty) {
            return AdmissionResponse(
                groupName = "NOT FILLED",
                userName = "NOT FILLED",
                role = RoleType.GUEST,
                entryRole = EntryRole.CANNOT_ATTEND,
                accessGranted = false,
                userEntity = user,
                formId = formId
            )
        }
        val responseEntity = response.orElseThrow()
        if (admissionComponent.onlyAcceptApprovedForms.isValueTrue() && (!responseEntity.accepted || responseEntity.rejected)) {
            return AdmissionResponse(
                groupName = "FILLED, NOT ACCEPTED",
                userName = "FILLED, NOT ACCEPTED",
                role = RoleType.GUEST,
                entryRole = EntryRole.CANNOT_ATTEND,
                accessGranted = false,
                userEntity = user,
                formId = formId,
                responseId = responseEntity.id
            )
        }

        val grants = mutableSetOf<EntryRole>()
        grants.add(EntryRole.USER)

        addGrantsByRole(user, grants)
        addGrantsByCmschId(user.cmschId.lowercase(), grants)
        addGrantsByGroup(user.groupName.lowercase(), grants)

        val grant = grants.maxByOrNull { it.value } ?: EntryRole.CANNOT_ATTEND
        log.info("Admission is ${if (grant.canAttend) "OK" else "DENIED"} for user '${user.fullName}' with group '${user.groupName}' as $grant")
        return AdmissionResponse(
            groupName = user.groupName,
            userName = user.fullName,
            role = user.role,
            entryRole = grant,
            accessGranted = grant.canAttend,
            userEntity = user,
            formId = formId,
            responseId = responseEntity.id
        )
    }

    private fun isBanned(cmschId: String, groupName: String): Boolean {
        if (cmschId.isNotEmpty() && admissionComponent.bannedUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId.lowercase())) {
            log.info("User $cmschId BANNED by user-ban-list")
            return true
        }
        if (groupName.isNotEmpty() && admissionComponent.bannedGroups.getValue().lowercase().split(Regex(", *")).contains(groupName.lowercase())) {
            log.info("User $cmschId BANNED by group-ban-list for they group: $groupName")
            return true
        }
        return false
    }

    private fun addGrantsByCmschId(
        cmschId: String,
        grants: MutableSet<EntryRole>
    ) {
        if (cmschId.isEmpty())
            return

        if (admissionComponent.userUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId))
            grants.add(EntryRole.USER)

        if (admissionComponent.vipUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId))
            grants.add(EntryRole.VIP)

        if (admissionComponent.performerUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId))
            grants.add(EntryRole.PERFORMER)

        if (admissionComponent.organizerUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId))
            grants.add(EntryRole.ORGANIZER)

        if (admissionComponent.leadOrganizerUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId))
            grants.add(EntryRole.LEAD_ORGANIZER)
    }

    private fun addGrantsByGroup(
        groupName: String,
        grants: MutableSet<EntryRole>
    ) {
        if (groupName.isEmpty())
            return

        if (admissionComponent.userGroups.getValue().lowercase().split(Regex(", *")).contains(groupName))
            grants.add(EntryRole.USER)

        if (admissionComponent.vipGroups.getValue().lowercase().split(Regex(", *")).contains(groupName))
            grants.add(EntryRole.VIP)

        if (admissionComponent.performerGroups.getValue().lowercase().split(Regex(", *")).contains(groupName))
            grants.add(EntryRole.PERFORMER)

        if (admissionComponent.organizerGroups.getValue().lowercase().split(Regex(", *")).contains(groupName))
            grants.add(EntryRole.ORGANIZER)

        if (admissionComponent.leadOrganizerGroups.getValue().lowercase().split(Regex(", *")).contains(groupName))
            grants.add(EntryRole.LEAD_ORGANIZER)
    }

    private fun addGrantsByRole(
        user: UserEntity,
        grants: MutableSet<EntryRole>
    ) {
        if (admissionComponent.grantUserByDefault.isValueTrue() && user.role.value >= RoleType.BASIC.value)
            grants.add(EntryRole.USER)

        if (admissionComponent.grantUserByAttendee.isValueTrue() && user.role.value >= RoleType.ATTENDEE.value)
            grants.add(EntryRole.USER)

        if (admissionComponent.grantUserByStaff.isValueTrue() && user.role.value >= RoleType.STAFF.value)
            grants.add(EntryRole.USER)

        if (admissionComponent.grantOrganizerByStaff.isValueTrue() && user.role.value >= RoleType.STAFF.value)
            grants.add(EntryRole.ORGANIZER)

        if (admissionComponent.grantOrganizerByAdmin.isValueTrue() && user.role.value >= RoleType.ADMIN.value)
            grants.add(EntryRole.ORGANIZER)
    }

}
