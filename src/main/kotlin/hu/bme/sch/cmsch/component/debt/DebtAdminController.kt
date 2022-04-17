package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.controller.AbstractAdminPanelController
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_DEBTS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_DEBTS
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_PRODUCTS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/component/debt")
@ConditionalOnBean(DebtComponent::class)
class DebtAdminController(
    adminMenuService: AdminMenuService,
    component: DebtComponent,
) : ComponentApiBase(
    adminMenuService,
    DebtComponent::class.java,
    component,
    PERMISSION_CONTROL_DEBTS,
    "Tartozások",
    "Tartozások testreszabása"
)

@Controller
@RequestMapping("/admin/control/products")
@ConditionalOnBean(DebtComponent::class)
class ProductController(
    repo: ProductRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent
) : AbstractAdminPanelController<ProductEntity>(
    repo,
    "products", "Termék", "Termékek",
    "Az összes vásárolható termék kezelése. Az eladáshoz külön felület tartozik!",
    ProductEntity::class, ::ProductEntity, importService, adminMenuService, component,
    permissionControl = PERMISSION_EDIT_PRODUCTS,
    importable = true, adminMenuIcon = "inventory_2"
)

@Controller
@RequestMapping("/admin/control/debts")
@ConditionalOnBean(DebtComponent::class)
class SoldProductController(
    repo: SoldProductRepository,
    private val clock: ClockService,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: DebtComponent
) : AbstractAdminPanelController<SoldProductEntity>(
    repo,
    "debts", "Tranzakció", "Tranzakciók",
    "Az összes eladásból származó tranzakciók.",
    SoldProductEntity::class, ::SoldProductEntity, importService, adminMenuService, component,
    controlMode = CONTROL_MODE_EDIT,
    permissionControl = PERMISSION_EDIT_DEBTS,
    importable = true, adminMenuIcon = "sync_alt"
) {
    override fun onEntityPreSave(entity: SoldProductEntity, request: HttpServletRequest) {
        val date = clock.getTimeInSeconds()
        val user = request.getUser()
        entity.log = "${entity.log} '${user.fullName}'(${user.id}) changed [shipped: ${entity.shipped}, payed: ${entity.payed}, finsihed: ${entity.finsihed}] at $date;"
    }
}
