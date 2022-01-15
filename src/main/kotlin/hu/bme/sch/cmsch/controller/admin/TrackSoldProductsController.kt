package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.dao.SoldProductRepository
import hu.bme.sch.cmsch.dto.virtual.ProductGroupVirtualEntity
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/track-sold")
class TrackSoldProductsController(
        private val productRepository: SoldProductRepository
) {

    private val view = "track-sold"
    private val titleSingular = "Eladott termékek"
    private val titlePlural = "Eladott termékek"
    private val description = "Az eladott termékek mennyiségei típusra rendezve"

    private val overviewDescriptor = OverviewBuilder(ProductGroupVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchData())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    private fun fetchData(): List<ProductGroupVirtualEntity> {
        return productRepository.findAll()
                .groupBy { it.product }
                .map { ProductGroupVirtualEntity(0, it.key, it.value.size) }
    }

}
