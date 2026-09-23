package hu.bme.sch.cmsch.component.bounty

import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import tools.jackson.databind.ObjectMapper

@Controller
@RequestMapping("/admin/control/bounty-registrations")
@ConditionalOnBean(BountyComponent::class)
class BountyRegistrationController(
    repo: BountyRegistrationRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: BountyComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<BountyRegistrationEntity>(
    "bounty-registrations",
    BountyRegistrationEntity::class, ::BountyRegistrationEntity,
    "Regisztráció", "Regisztrációk",
    "A Fejvadászat regisztrációk listája (regisztrálni a QR olvasóval lehet)",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_BOUNTY_REGISTRATIONS,
    createPermission = StaffPermissions.PERMISSION_CREATE_BOUNTY_REGISTRATIONS,
    editPermission = StaffPermissions.PERMISSION_EDIT_BOUNTY_REGISTRATIONS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_BOUNTY_REGISTRATIONS,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = true,

    adminMenuIcon = "how_to_reg",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<BountyRegistrationEntity>(false)
)
