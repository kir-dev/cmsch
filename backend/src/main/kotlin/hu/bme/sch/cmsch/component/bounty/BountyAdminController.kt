package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.config.StartupPropertyConfig
import hu.bme.sch.cmsch.dto.ResolveRequest
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/bounty-registration")
@CrossOrigin(originPatterns = ["*"], allowedHeaders = ["*"], allowCredentials = "false")
@ConditionalOnBean(BountyComponent::class)
class BountyAdminController(
    private val bountyService: BountyService,
    private val bountyComponent: BountyComponent,
    private val startupPropertyConfig: StartupPropertyConfig,
    private val auditLogService: AuditLogService,
    private val adminMenuService: AdminMenuService,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @GetMapping("/")
    fun registrationScanner(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        if (StaffPermissions.PERMISSION_REGISTER_BOUNTY.validate(user).not()) {
            adminMenuService.addPartsForMenu(user, model)
            model.addAttribute("permission", StaffPermissions.PERMISSION_REGISTER_BOUNTY.permissionString)
            model.addAttribute("user", user)

            auditLogService.admin403(
                user, bountyComponent.component,
                "GET /bounty-registration", StaffPermissions.PERMISSION_REGISTER_BOUNTY.permissionString
            )
            return "admin403"
        }

        model.addAttribute("prefix", startupPropertyConfig.profileQrPrefix)
        model.addAttribute("bountyPrefix", BOUNTY_QR_PREFIX)
        model.addAttribute("resolveUrl", "/resolve")
        model.addAttribute("registerUrl", "/register")
        return "bounty-registration"
    }

    @ResponseBody
    @PostMapping("/resolve")
    fun resolve(@RequestBody resolve: ResolveRequest, auth: Authentication): BountyRegistrationResponse {
        val user = auth.getUser()
        if (!StaffPermissions.PERMISSION_REGISTER_BOUNTY.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        log.info("Previewing bounty registration for: {}", resolve.cmschId)
        return bountyService.previewRegistrationByCmschId(resolve.cmschId)
    }

    @ResponseBody
    @PostMapping("/register")
    fun register(@RequestBody resolve: ResolveRequest, auth: Authentication): BountyRegistrationResponse {
        val user = auth.getUser()
        if (!StaffPermissions.PERMISSION_REGISTER_BOUNTY.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        log.info("Registering bounty participant: {}", resolve.cmschId)
        return bountyService.registerByCmschId(resolve.cmschId)
    }

}
