package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_TOKEN
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_TOKENS
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/token")
@ConditionalOnBean(TokenComponent::class)
class TokenAdminController(
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    TokenComponent::class.java,
    component,
    PERMISSION_CONTROL_TOKEN,
    "Tokenek",
    "Tokenek testreszabása",
    menuService = menuService
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
    permissionControl = PERMISSION_EDIT_TOKENS,
    importable = true, adminMenuIcon = "token"
)

