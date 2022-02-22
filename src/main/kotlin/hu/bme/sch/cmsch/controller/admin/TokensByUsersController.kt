package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.TokenPropertyRepository
import hu.bme.sch.cmsch.dto.virtual.TokenListByUserVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.TokenVirtualEntity
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/admin/control/token-properties-user")
class TokensByUsersController(
        private val tokenPropertyRepository: TokenPropertyRepository,
        private val groupRepository: GroupRepository
) {

    private val view = "token-properties-user"
    private val titleSingular = "Tokenek felhasználónként"
    private val titlePlural = "Tokenek felhasználónként"
    private val description = "Beolvasott tokenek felhasználónként csoportosítva"

    private val overviewDescriptor = OverviewBuilder(TokenListByUserVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(TokenVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantMedia }?.not() ?: true) {
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

    private fun fetchOverview(): List<TokenListByUserVirtualEntity> {
        return tokenPropertyRepository.findAll().groupBy { it.ownerUser?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].ownerUser?.id ?: 0).map { it.name }.orElse("n/a")
                    TokenListByUserVirtualEntity(
                            it[0].ownerUser?.id ?: 0,
                            it[0].ownerUser?.fullName ?: "n/a",
                            groupName,
                            it.count()
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantMedia }?.not() != false) {
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
        model.addAttribute("controlMode", CONTROL_MODE_DELETE)

        return "overview"
    }

    private fun fetchProperties(user: Int): List<TokenVirtualEntity> {
        return tokenPropertyRepository.findAllByOwnerUser_Id(user)
            .map {
                TokenVirtualEntity(
                    it.id,
                    it.token?.title ?: "n/a",
                    it.token?.type ?: "n/a",
                    it.recieved
                )
            }
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantMedia }?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", request.getUser())

        val entity = tokenPropertyRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("item", entity.orElseThrow().toString())
        }
        return "delete"
    }

    @PostMapping("/delete/{id}")
    fun delete(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantRiddle }?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        val entity = tokenPropertyRepository.findById(id).orElseThrow()
        tokenPropertyRepository.delete(entity)
        return "redirect:/admin/control/$view/"
    }

}
