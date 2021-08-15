package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.model.ProductEntity
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.service.ProductService
import hu.bme.sch.g7.util.getUser
import hu.bme.sch.g7.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_SELL = "sell"

@Controller
@RequestMapping("/admin/control")
class SellProductsController(
        private val productService: ProductService
) {

    private val descriptor = OverviewBuilder(ProductEntity::class)

    private val titleSingular = "Termék"
    private val titlePlural = "Termékek"
    private val description = "Válassz terméket az eladáshoz"

    @GetMapping("/sell-food")
    fun sellFood(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantSellFood }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
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
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantSellMerch }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", "sell-merch")
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllMerch())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_SELL)

        return "overview"
    }

    @GetMapping("/sell-products")
    fun sellProduct(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantSellProduct }?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", "sell-merch")
        model.addAttribute("columns", descriptor.getColumns())
        model.addAttribute("fields", descriptor.getColumnDefinitions())
        model.addAttribute("rows", productService.getAllProducts())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_SELL)

        return "overview"
    }

}