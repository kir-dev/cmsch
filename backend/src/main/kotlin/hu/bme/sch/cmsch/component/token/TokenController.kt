package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/tokens")
@ConditionalOnBean(TokenComponent::class)
class TokenController(
    repo: TokenRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : OneDeepEntityPage<TokenEntity>(
    "tokens",
    TokenEntity::class, ::TokenEntity,
    "Token", "Tokenek",
    "Képrejtvény kategóriák kezelése.",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_TOKENS,
    createPermission = StaffPermissions.PERMISSION_CREATE_TOKENS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_TOKENS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_TOKENS,

    createEnabled = true,
    editEnabled   = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "token",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<TokenEntity>(false)
)
