package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.admin.CONTROL_MODE_NONE
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.PERMISSION_SHOW_SOLD_STATS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/track-sold")
@ConditionalOnBean(DebtComponent::class)
class SoldProductStatsAdminController(
    private val productRepository: SoldProductRepository,
    private val adminMenuService: AdminMenuService
) {

    private val view = "track-sold"
    private val titleSingular = "Eladott termékek"
    private val titlePlural = "Eladott termékek"
    private val description = "Az eladott termékek mennyiségei típusra rendezve"
    private val permissionControl = PERMISSION_SHOW_SOLD_STATS

    private val overviewDescriptor = OverviewBuilder(ProductGroupVirtualEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "receipt_long",
                "/admin/control/${view}",
                5,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchData())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    private fun fetchData(): List<ProductGroupVirtualEntity> {
        return productRepository.findAll()
                .groupBy { it.product }
                .map { ProductGroupVirtualEntity(0, it.key, it.value.size) }
    }

}
