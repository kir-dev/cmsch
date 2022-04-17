package hu.bme.sch.cmsch.component.debt

import hu.bme.sch.cmsch.admin.INPUT_TYPE_FILE
import hu.bme.sch.cmsch.admin.INTERPRETER_INHERIT
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.ClockService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_DEBTS
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import javax.servlet.http.HttpServletRequest
import kotlin.reflect.KMutableProperty1

@Controller
@RequestMapping("/admin/control/debts-by-users")
@ConditionalOnBean(DebtComponent::class)
class DebtAdminDebtsByUsersController(
    private val soldProductRepository: SoldProductRepository,
    private val groupRepository: GroupRepository,
    private val clock: ClockService,
    private val adminMenuService: AdminMenuService
) {

    private val view = "debts-by-users"
    private val titleSingular = "Tartozás"
    private val titlePlural = "Felh. tartozásai"
    private val description = "Tartozások felhasználónként csoportosítva"
    private val permissionControl = PERMISSION_EDIT_DEBTS

    private val entitySourceMapping: Map<String, (SoldProductEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val overviewDescriptor = OverviewBuilder(DebtsByUserVirtualEntity::class)
    private val submittedDescriptor = OverviewBuilder(SoldProductEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            DebtComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "account_balance",
                "/admin/control/${view}",
                12,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", overviewDescriptor.getColumns())
        model.addAttribute("fields", overviewDescriptor.getColumnDefinitions())
        model.addAttribute("rows", fetchOverview())
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_VIEW)

        return "overview"
    }

    private fun fetchOverview(): List<DebtsByUserVirtualEntity> {
        return soldProductRepository.findAll().groupBy { it.ownerId }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].responsibleGroupId).map { it.name }.orElse("n/a")
                    DebtsByUserVirtualEntity(
                            it[0].ownerId,
                            it[0].ownerName,
                            groupName,
                            it.sumOf { it.price },
                            it.filter { !it.payed }.sumOf { it.price },
                            it.filter { !it.finsihed }.sumOf { it.price }
                    )
                }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("columns", submittedDescriptor.getColumns())
        model.addAttribute("fields", submittedDescriptor.getColumnDefinitions())
        model.addAttribute("rows", soldProductRepository.findAllByOwnerId(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        return "overview"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, request: HttpServletRequest): String {
        val user = request.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", true)
        model.addAttribute("view", view)
        model.addAttribute("id", id)
        model.addAttribute("inputs", submittedDescriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        val entity = soldProductRepository.findById(id)
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
             model: Model,
             request: HttpServletRequest
    ): String {
        val user = request.getUser()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = soldProductRepository.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }

        val date = clock.getTimeInSeconds()
        val transaction = entity.get()
        updateEntity(submittedDescriptor, transaction, dto)
        transaction.log = "${transaction.log} '${user.fullName}'(${user.id}) changed [shipped: ${transaction.shipped}, payed: ${transaction.payed}, finsihed: ${transaction.finsihed}] at $date;"
        transaction.id = id
        soldProductRepository.save(transaction)
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
