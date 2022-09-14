package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.component.task.SubmittedTaskEntity
import hu.bme.sch.cmsch.component.task.SubmittedTaskRepository
import hu.bme.sch.cmsch.component.task.TaskComponent
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.CONTROL_MODE_DELETE
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

@Controller
@RequestMapping("/admin/control/raw-token-properties")
@ConditionalOnBean(TokenComponent::class)
class TokenSubmissionsController(
    private val repo: TokenPropertyRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent
) : AbstractAdminPanelController<TokenPropertyRawView>(
    null,
    "raw-token-properties", "Nyers beolvasás", "Nyers beolvasások",
    "Nyers token beolvasások",
    TokenPropertyRawView::class, ::TokenPropertyRawView,
    importService, adminMenuService, component,
    permissionControl = PERMISSION_CONTROL_TOKEN,
    importable = false, adminMenuPriority = 6, adminMenuIcon = "raw_on",
    controlMode = CONTROL_MODE_DELETE,
    allowedToPurge = true,
    purgeRepo = repo,
    savable = true
) {

    override fun fetchOverview(): Iterable<TokenPropertyRawView> {
        return repo.findAll().map { TokenPropertyRawView(
            it.id,
            it.ownerUser?.id ?: 0,
            it.ownerUser?.userName ?: "",
            it.ownerGroup?.id ?: 0,
            it.ownerGroup?.name ?: "",
            it.token?.token ?: "no-token",
            it.recieved
        ) }
    }

}
