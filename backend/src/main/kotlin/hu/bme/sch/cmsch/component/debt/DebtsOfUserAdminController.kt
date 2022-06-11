package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ImplicitPermissions.PERMISSION_IMPLICIT_ANYONE
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct

@Controller
@RequestMapping("/admin/control/my-debts")
@ConditionalOnBean(DebtComponent::class)
class DebtsOfUserAdminController(
    private val productService: ProductService,
    private val adminMenuService: AdminMenuService
) {

    private val view = "my-debts"
    private val titlePlural = "Saját tartozásaim"
    private val permissionControl = PERMISSION_IMPLICIT_ANYONE

    private val debtsDescriptor = OverviewBuilder(SoldProductEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "account_balance_wallet",
                "/admin/control/${view}",
                6,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun myDebts(model: Model, auth: Authentication): String {
        val user = auth.getUserFromDatabase()
        adminMenuService.addPartsForMenu(user, model)
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", "Saját tartozásaim")
        model.addAttribute("description", "Ezekkel a tételekkel a reszortgdaságisnak kell elszámolnod! A pontos módról emailben értesülhetsz.")
        model.addAttribute("view", view)
        model.addAttribute("columns", debtsDescriptor.getColumns())
        model.addAttribute("fields", debtsDescriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllDebtsByUser(user))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

}
