package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SELL_ANY_PRODUCT
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SELL_FOOD
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_SELL_MERCH
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_SELL = "sell"

private const val SELL_PRODUCT_TITLE = "Termék árusítás"
private const val SELL_FOOD_TITLE = "Étel árusítás"
private const val SELL_MERCH_TITLE = "Merch árusítás"

@Controller
@RequestMapping("/admin/control")
@ConditionalOnBean(DebtComponent::class)
class SellProductsController(
    private val productService: ProductService,
    private val adminMenuService: AdminMenuService
) {

    private val descriptor = OverviewBuilder(ProductEntity::class)
    private val description = "Válassz terméket az eladáshoz"

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                SELL_PRODUCT_TITLE,
                "point_of_sale",
                "/admin/control/sell-products",
                2,
                PERMISSION_SELL_ANY_PRODUCT
            )
        )

        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                SELL_FOOD_TITLE,
                "restaurant",
                "/admin/control/sell-food",
                3,
                PERMISSION_SELL_FOOD
            )
        )

        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                SELL_MERCH_TITLE,
                "sell",
                "/admin/control/sell-products",
                4,
                PERMISSION_SELL_MERCH
            )
        )
    }

    @GetMapping("/sell-products")
    fun sellProduct(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (PERMISSION_SELL_ANY_PRODUCT.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_SELL_ANY_PRODUCT.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", SELL_PRODUCT_TITLE)
        model.addAttribute("titleSingular", SELL_PRODUCT_TITLE)
        model.addAttribute("description", description)
        model.addAttribute("view", "sell-merch")
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllProducts())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_SELL)

        return "overview"
    }

    @GetMapping("/sell-food")
    fun sellFood(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (PERMISSION_SELL_FOOD.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_SELL_FOOD.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", SELL_FOOD_TITLE)
        model.addAttribute("titleSingular", SELL_FOOD_TITLE)
        model.addAttribute("description", description)
        model.addAttribute("view", "sell-food")
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllFoods())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_SELL)

        return "overview"
    }

    @GetMapping("/sell-merch")
    fun sellMerch(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (PERMISSION_SELL_MERCH.validate(user).not()) {
            model.addAttribute("permission", PERMISSION_SELL_MERCH.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", SELL_MERCH_TITLE)
        model.addAttribute("titleSingular", SELL_MERCH_TITLE)
        model.addAttribute("description", description)
        model.addAttribute("view", "sell-merch")
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllMerch())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_SELL)

        return "overview"
    }

}
