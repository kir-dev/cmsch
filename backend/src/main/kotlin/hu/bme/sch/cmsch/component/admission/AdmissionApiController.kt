package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.component.bmejegy.BmejegyRecordEntity
import hu.bme.sch.cmsch.component.bmejegy.BmejegyService
import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.ResolveRequest
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.UserService
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
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
    private val bmejegyService: Optional<BmejegyService>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun admision(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_VALIDATE_ADMISSION.validate(user).not()) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_VALIDATE_ADMISSION.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        return "admission"
    }

    @ResponseBody
    @PostMapping("/resolve")
    fun resolve(@RequestBody resolve: ResolveRequest): AdmissionResponse {
        log.info("Resolving admission for: ${resolve.cmschId}")
        if (resolve.cmschId.startsWith(startupPropertyConfig.profileQrPrefix)) {
            return userService.searchByCmschId(resolve.cmschId)
                .map { mapUser(it) }
                .orElse(
                    AdmissionResponse(
                        "NOT FOUND BY CMSCHID", "NOT FOUND BY CMSCH",
                        RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
                    )
                )

        } else if (bmejegyService.isPresent) {
            val ticket = bmejegyService.flatMap { it.findUserByVoucher(resolve.cmschId) }
            return if (ticket.isEmpty) {
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
            return AdmissionResponse(
                "NOT FOUND AT ALL", "NOT FOUND AT ALL",
                RoleType.GUEST, EntryRole.CANNOT_ATTEND, false
            )
        }
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

    private fun mapUser(user: UserEntity): AdmissionResponse {
        if (isBanned(user.cmschId, user.groupName))
            return AdmissionResponse(user.groupName, user.fullName, user.role, EntryRole.BANNED, false)

        val grants = mutableSetOf<EntryRole>()

        addGrantsByRole(user, grants)
        addGrantsByCmschId(user.cmschId.lowercase(), grants)
        addGrantsByGroup(user.groupName.lowercase(), grants)

        val grant = grants.maxByOrNull { it.value } ?: EntryRole.CANNOT_ATTEND
        log.info("Admission is ${if (grant.canAttend) "OK" else "DENIED"} for user '${user.fullName}' with group '${user.groupName}' as $grant")
        return AdmissionResponse(user.groupName, user.fullName, user.role, grant, grant.canAttend)
    }

    private fun isBanned(cmschId: String, groupName: String): Boolean {
        if (admissionComponent.bannedUsers.getValue().lowercase().split(Regex(", *")).contains(cmschId.lowercase())) {
            log.info("User $cmschId BANNED by user-ban-list")
            return true
        }
        if (admissionComponent.bannedGroups.getValue().lowercase().split(Regex(", *")).contains(groupName.lowercase())) {
            log.info("User $cmschId BANNED by group-ban-list for they group: $groupName")
            return true
        }
        return false
    }

    private fun addGrantsByCmschId(
        cmschId: String,
        grants: MutableSet<EntryRole>
    ) {
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
