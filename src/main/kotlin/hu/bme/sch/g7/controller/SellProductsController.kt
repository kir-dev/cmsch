package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.model.ProductEntity
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.service.ProductService
import hu.bme.sch.g7.util.getUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

const val CONTROL_MODE_SELL = "sell"

@Controller
@RequestMapping("/admin/control")
class SellProductsController(
        val productService: ProductService
) {

    private val descriptor = OverviewBuilder(ProductEntity::class)

    val titleSingular = "Termék"
    val titlePlural = "Termékek"
    val description = "Válassz terméket az eladáshoz"

    @GetMapping("/sell-food")
    fun sellFood(model: Model, request: HttpServletRequest): String {
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