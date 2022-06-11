package hu.bme.sch.cmsch.component.signup

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_SIGNUP
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/signup")
@ConditionalOnBean(SignupComponent::class)
class SignupAdminController(
    adminMenuService: AdminMenuService,
    component: SignupComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    SignupComponent::class.java,
    component,
    PERMISSION_CONTROL_SIGNUP,
    "Jelentkezések",
    "Jelentkezések",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/forms")
@ConditionalOnBean(SignupComponent::class)
class SignupController(
    repo: SignupFormRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: SignupComponent
) : AbstractAdminPanelController<SignupFormEntity>(
    repo,
    "forms", "Űrlap", "Űrlapok",
    "Űrlapok az eseményre vagy annak részprogramjára való jelentkezéshez",
    SignupFormEntity::class, ::SignupFormEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_SIGNUP_RESULTS,
    importable = true, adminMenuIcon = "view_list"
)
