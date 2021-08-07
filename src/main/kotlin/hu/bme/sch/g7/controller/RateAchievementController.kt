package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.INPUT_TYPE_FILE
import hu.bme.sch.g7.admin.INTERPRETER_INHERIT
import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dao.SubmittedAchievementRepository
import hu.bme.sch.g7.dto.virtual.GradedAchievementGroup
import hu.bme.sch.g7.model.SubmittedAchievementEntity
import hu.bme.sch.g7.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KMutableProperty1

const val CONTROL_MODE_RATE = "rate"
const val CONTROL_MODE_GRADE = "grade"

@Controller
@RequestMapping("/admin/control/rate-achievements")
class RateAchievementController(
        val submittedRepository: SubmittedAchievementRepository
) {

    val view = "rate-achievements"
    val titleSingular = "Értékelés"
    val titlePlural = "Értékelések"
    val description = "A tankörök által beadott értékelések"

    val entitySourceMapping: Map<String, (SubmittedAchievementEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val log = LoggerFactory.getLogger(javaClass)
    private val overviewDescriptor = OverviewBuilder(GradedAchievementGroup::class)
    private val submittedDescriptor = OverviewBuilder(SubmittedAchievementEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_RATE)

        return "overview"
    }

    private fun fetchOverview(): List<GradedAchievementGroup> {
        return submittedRepository.findAll().groupBy { it.achievement }
                .map { it.value }
                .filter { !it.isEmpty() }
                .map {
                    GradedAchievementGroup(
                            it[0].achievement?.id ?: 0,
                            it[0].achievement?.title ?: "n/a",
                            it.count { it.approved },
                            it.count { it.rejected },
                            it.count { !it.approved && !it.rejected }
                    )
                }
                .sortedByDescending { it.notGraded }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", submittedRepository.findByAchievement_Id(id))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    @GetMapping("/rate/{id}")
    fun rate(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", submittedRepository.findByAchievement_IdAndRejectedIsFalseAndApprovedIsFalse(id))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        return "overview"
    }

    @GetMapping("/grade/{id}")
    fun edit(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", submittedDescriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_GRADE)

        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
            model.addAttribute("comment", "Feladványhoz tartozó max pont: " + (entity.orElseThrow().achievement?.maxScore ?: "n/a"))
        }
        return "details"
    }

    @PostMapping("/grade/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: SubmittedAchievementEntity
    ): String {
        val entity = submittedRepository.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/grade/$id"
        }

        updateEntity(submittedDescriptor, entity.get(), dto)
        if (entity.get().approved && entity.get().rejected)
            entity.get().rejected = false
        entity.get().id = id
        submittedRepository.save(entity.get())
        return "redirect:/admin/control/$view"
    }

    private fun updateEntity(
            descriptor: OverviewBuilder,
            entity: SubmittedAchievementEntity,
            dto: SubmittedAchievementEntity
    ) {
        descriptor.getInputs().forEach {
            if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore) {
                when {
                    it.second.interpreter == INTERPRETER_INHERIT && it.second.type != INPUT_TYPE_FILE -> {
                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, it.first.getter.call(dto))
                    }
                }
            }
        }
    }

}