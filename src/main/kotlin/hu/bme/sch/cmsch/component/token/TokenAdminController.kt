package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PERMISSION_CONTROL_TOKEN
import hu.bme.sch.cmsch.service.PermissionValidator
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/token")
@ConditionalOnBean(TokenComponent::class)
class TokenAdminController(
    adminMenuService: AdminMenuService,
    component: TokenComponent,
) : ComponentApiBase(
    adminMenuService,
    TokenComponent::class.java,
    component,
    PERMISSION_CONTROL_TOKEN,
    "Tokenek",
    "Tokenek testreszabása"
)

@Controller
@RequestMapping("/admin/control/tokens")
@ConditionalOnBean(TokenComponent::class)
class TokenController(
    repo: TokenRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent
) : AbstractAdminPanelController<TokenEntity>(
    repo,
    "tokens", "Token", "Tokenek",
    "Képrejtvény kategóriák kezelése.",
    TokenEntity::class, ::TokenEntity, importService, adminMenuService, component,
    permissionControl = PermissionValidator() { it.isAdmin() ?: false || it.grantMedia ?: false },
    importable = true, adminMenuIcon = "token"
)

