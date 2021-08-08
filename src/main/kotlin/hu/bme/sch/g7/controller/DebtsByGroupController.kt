package hu.bme.sch.g7.controller

import hu.bme.sch.g7.admin.INPUT_TYPE_FILE
import hu.bme.sch.g7.admin.INTERPRETER_INHERIT
import hu.bme.sch.g7.admin.OverviewBuilder
import hu.bme.sch.g7.dao.GroupRepository
import hu.bme.sch.g7.dao.SoldProductRepository
import hu.bme.sch.g7.dao.SubmittedAchievementRepository
import hu.bme.sch.g7.dto.virtual.DebtsByGroup
import hu.bme.sch.g7.dto.virtual.GradedAchievementGroup
import hu.bme.sch.g7.model.SoldProductEntity
import hu.bme.sch.g7.model.SubmittedAchievementEntity
import hu.bme.sch.g7.util.getUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.Instant
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KMutableProperty1

@Controller
@RequestMapping("/admin/control/debts-by-group")
class DebtsByGroupController(
        val soldProductController: SoldProductRepository,
        val groupRepository: GroupRepository
) {

    val view = "debts-by-group"
    val titleSingular = "Értékelés"
    val titlePlural = "Értékelések"
    val description = "A tankörök által beadott értékelések"

    val entitySourceMapping: Map<String, (SoldProductEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val overviewDescriptor = OverviewBuilder(DebtsByGroup::class)
    private val submittedDescriptor = OverviewBuilder(SoldProductEntity::class)

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
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<DebtsByGroup> {
        return soldProductController.findAll().groupBy { it.responsibleGroupId }
                .map { it.value }
                .filter { !it.isEmpty() }
                .map {
                    val groupName = groupRepository.findById(it[0].responsibleGroupId).map { it.name }.orElse("n/a")
                    DebtsByGroup(
                            it[0].responsibleGroupId,
                            groupName,
                            it.sumOf { it.price },
                            it.filter { !it.payed }.sumOf { it.price },
                            it.filter { !it.finsihed }.sumOf { it.price }
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", soldProductController.findAllByResponsibleGroupId(id))
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        return "overview"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", submittedDescriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        val entity = soldProductController.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
        }
        return "details"
    }

    @PostMapping("/edit/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: SoldProductEntity,
             request: HttpServletRequest
    ): String {
        val entity = soldProductController.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }

        val user = request.getUser()
        val date = Instant.now().toEpochMilli()
        val transaction = entity.get()
        updateEntity(submittedDescriptor, transaction, dto)
        transaction.log = "${transaction.log} '${user.fullName}'(${user.id}) changed [shipped: ${transaction.shipped}, payed: ${transaction.payed}, finsihed: ${transaction.finsihed}] at $date;"
        transaction.id = id
        soldProductController.save(transaction)
        return "redirect:/admin/control/$view"
    }

    private fun updateEntity(
            descriptor: OverviewBuilder,
            entity: SoldProductEntity,
            dto: SoldProductEntity
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