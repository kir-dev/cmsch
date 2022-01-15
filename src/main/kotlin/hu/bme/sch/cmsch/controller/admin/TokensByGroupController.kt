package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.dao.GroupRepository
import hu.bme.sch.cmsch.dao.TokenPropertyRepository
import hu.bme.sch.cmsch.dto.virtual.TokenListByGroupVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.TokenVirtualEntity
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/token-properties-group")
class TokensByGroupController(
    private val tokenPropertyRepository: TokenPropertyRepository,
    private val groupRepository: GroupRepository,
    private val clock: ClockService
) {

    private val view = "token-properties-group"
    private val titleSingular = "Tokenek tankörönként"
    private val titlePlural = "Tokenek tankörönként"
    private val description = "Tokenek tankörönként csoportosítva"

    private val overviewDescriptor = OverviewBuilder(TokenListByGroupVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(TokenVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<TokenListByGroupVirtualEntity> {
        return tokenPropertyRepository.findAll().groupBy { it.ownerGroup?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].ownerGroup?.id ?: 0).map { it.name }.orElse("n/a")
                    TokenListByGroupVirtualEntity(
                            it[0].ownerGroup?.id ?: 0,
                            groupName,
                            it.count()
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isAdmin()?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", propertyDescriptor.getColumns())
        model.addAttribute("fields", propertyDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchProperties(id))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_NONE)

        return "overview"
    }

    private fun fetchProperties(group: Int): List<TokenVirtualEntity> {
        return tokenPropertyRepository.findAllByOwnerGroup_Id(group)
            .map { TokenVirtualEntity(it.id, it.token?.title ?: "n/a", it.token?.type ?: "n/a") }
    }


}
