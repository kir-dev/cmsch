package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_FORM
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/form")
@ConditionalOnBean(FormComponent::class)
class FormComponentController(
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

