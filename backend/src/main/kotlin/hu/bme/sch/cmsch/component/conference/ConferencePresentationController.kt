package hu.bme.sch.cmsch.component.conference

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/conference-presentation")
@ConditionalOnBean(ConferenceComponent::class)
class ConferencePresentationController(
    repo: ConferencePresentationRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ConferenceComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<ConferencePresentationEntity>(
    "conference-presentation",
    ConferencePresentationEntity::class, ::ConferencePresentationEntity,
    "Előadás", "Előadások",
    "Konferencia előadásai",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_CONFERENCE,
    createPermission = StaffPermissions.PERMISSION_CREATE_CONFERENCE,
    editPermission =   StaffPermissions.PERMISSION_EDIT_CONFERENCE,
    deletePermission = StaffPermissions.PERMISSION_DELETE_CONFERENCE,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "school",
    adminMenuPriority = 4,

    searchSettings = calculateSearchSettings<ConferencePresentationEntity>(false)
)
