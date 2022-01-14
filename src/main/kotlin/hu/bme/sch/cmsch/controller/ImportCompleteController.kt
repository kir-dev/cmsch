package hu.bme.sch.cmsch.controller

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.dao.SoldProductRepository
import hu.bme.sch.cmsch.dto.virtual.ImportDebtsCompleteVirtualEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.SoldProductEntity
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserOrNull
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KProperty1

@Controller
@RequestMapping("/admin/control/import-debts-complete")
class ImportCompleteController(
        private val transactions: SoldProductRepository,
        private val clock: ClockService
) {

    private val view = "import-debts-complete"
    private val titleSingular = "Import"
    private val titlePlural = "[EXP] Tartozás importálása"
    private val description = "Tartozások importálása, experimental és nem transactional!"

    private val entitySourceMapping: Map<String, (SoldProductEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val descriptor = OverviewBuilder(ImportDebtsCompleteVirtualEntity::class)

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isSuperuser()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", listOf<String>())
        model.addAttribute("fields", listOf<Pair<KProperty1<out Any, *>, GenerateOverview>>())
        model.addAttribute("rows", listOf<ManagedEntity>())
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_EDIT_DELETE)

        return "overview"
    }

    @GetMapping("/create")
    fun create(model: Model, request: HttpServletRequest): String {
        if (request.getUserOrNull()?.isSuperuser()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", false)
        model.addAttribute("view", view)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("data", null)
        model.addAttribute("user", request.getUser())
        model.addAttribute("controlMode", CONTROL_MODE_EDIT_DELETE)

        return "details"
    }

    @ResponseBody
    @PostMapping("/create")
    fun create(@ModelAttribute(binding = false) dto: ImportDebtsCompleteVirtualEntity,
               model: Model,
               request: HttpServletRequest
    ): String {
        if (request.getUserOrNull()?.isSuperuser()?.not() ?: true) {
            model.addAttribute("user", request.getUser())
            return "403"
        }

        return dto.transactionIds.split(Regex("[\\n\\r]"))
                .asSequence()
                .filter { it.isNotBlank() }
                .map {
                    println("Setting #$it")
                    return@map transactions.findById(it.toInt()).map {
                        if (it.payed && it.finsihed) {
                            println("Already set #${it.id}")
                            return@map "${it.id}: already;"
                        } else {
                            it.payed = true
                            it.payedAt = clock.getTimeInSeconds()
                            it.finsihed = true
                            it.log += " autoclose by importer: ${request.getUser().fullName} at ${clock.getTimeInSeconds()};"
                            transactions.save(it)
                            println("Closed #${it.id}")
                            return@map "${it.id}: closed;"
                        }
                    }.orElse("$it: not found;")
                }
                .joinToString("\n")

    }

}
