package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.repository.RiddleMappingRepository
import hu.bme.sch.cmsch.repository.RiddleRepository
import hu.bme.sch.cmsch.dto.virtual.RiddleMappingVirtualEntity
import hu.bme.sch.cmsch.dto.virtual.RiddleStatsVirtualEntity
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
@RequestMapping("/admin/control/riddles-by-users")
class RiddlesByUsersController(
    private val riddleMappingRepository: RiddleMappingRepository,
    private val riddleRepository: RiddleRepository,
) {

    private val view = "riddles-by-users"
    private val titleSingular = "Riddle beadás felhasználónként"
    private val titlePlural = "Riddle beadások felhasználónként"
    private val description = "Beadott riddleök felhasználónként csoportosítva"

    private val overviewDescriptor = OverviewBuilder(RiddleStatsVirtualEntity::class)
    private val propertyDescriptor = OverviewBuilder(RiddleMappingVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantRiddle }?.not() ?: true) {
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

    private fun fetchOverview(): List<RiddleStatsVirtualEntity> {
        return riddleMappingRepository.findAll().groupBy { it.ownerUser?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    RiddleStatsVirtualEntity(
                            submissions[0].ownerUser?.id ?: 0,
                            submissions[0].ownerUser?.fullName ?: "n/a",
                            submissions.count { it.completed },
                            submissions.count { it.hintUsed }
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantRiddle }?.not() != false) {
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

    private fun fetchProperties(user: Int): List<RiddleMappingVirtualEntity> {
        return riddleMappingRepository.findAllByOwnerUser_Id(user)
            .map { submission ->
                RiddleMappingVirtualEntity(
                    submission.id,
                    submission.riddle?.categoryId ?: 0,
                    submission.riddle?.title ?: "n/a",
                    submission.hintUsed,
                    submission.completed,
                    submission.attemptCount,
                    submission.completedAt
                )
            }
    }

    @GetMapping("/delete/{id}")
    fun deleteConfirm(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.let { it.isAdmin() || it.grantRiddle }?.not() != false) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("user", request.getUser())

        val entity = riddleMappingRepository.findById(id)
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

        val entity = riddleMappingRepository.findById(id).orElseThrow()
        riddleMappingRepository.delete(entity)
        return "redirect:/admin/control/$view/"
    }

}
