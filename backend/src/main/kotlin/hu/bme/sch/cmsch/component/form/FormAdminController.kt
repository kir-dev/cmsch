package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_FORM
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/form")
@ConditionalOnBean(FormComponent::class)
class FormAdminController(
    adminMenuService: AdminMenuService,
    component: FormComponent,
    menuService: MenuService
) : ComponentApiBase(
    adminMenuService,
    FormComponent::class.java,
    component,
    PERMISSION_CONTROL_FORM,
    "Jelentkezések",
    "Jelentkezések",
    menuService = menuService
)

@Controller
@RequestMapping("/admin/control/forms")
@ConditionalOnBean(FormComponent::class)
class FormController(
    repo: FormRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: FormComponent
) : AbstractAdminPanelController<FormEntity>(
    repo,
    "forms", "Űrlap", "Űrlapok",
    "Űrlapok az eseményre vagy annak részprogramjára való jelentkezéshez",
    FormEntity::class, ::FormEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_SIGNUP_RESULTS,
    importable = true, adminMenuIcon = "view_list"
)
