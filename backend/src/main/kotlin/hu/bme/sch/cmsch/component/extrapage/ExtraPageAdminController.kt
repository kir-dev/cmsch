package hu.bme.sch.cmsch.component.extrapage

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_EXTRA_PAGES
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_EXTRA_PAGES
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/extraPage")
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageAdminController(
    adminMenuService: AdminMenuService,
    component: ExtraPageComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    ExtraPageComponent::class.java,
    component,
    PERMISSION_CONTROL_EXTRA_PAGES,
    "Oldalak",
    "Oldalak testreszabása",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/extra-pages")
@ConditionalOnBean(ExtraPageComponent::class)
class ExtraPageController(
    repo: ExtraPageRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ExtraPageComponent
) : AbstractAdminPanelController<ExtraPageEntity>(
    repo,
    "extra-pages", "Extra Oldal", "Extra oldalak",
    "Egyedi oldalak kezelése.",
    ExtraPageEntity::class, ::ExtraPageEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_EXTRA_PAGES,
    adminMenuIcon = "article"
) {

    override fun filterOverview(user: CmschUser, rows: Iterable<ExtraPageEntity>): Iterable<ExtraPageEntity> {
        return rows.filter { editPermissionCheck(user, it) }
    }

    override fun editPermissionCheck(user: CmschUser, entity: ExtraPageEntity): Boolean {
        return user.isAdmin() || entity.permissionToEdit.isBlank() || user.hasPermission(entity.permissionToEdit)
    }

}
