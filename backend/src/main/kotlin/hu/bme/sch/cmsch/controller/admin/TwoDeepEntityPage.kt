package hu.bme.sch.cmsch.controller.admin

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.IdentifiableEntity
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.PermissionValidator
import hu.bme.sch.cmsch.util.getUser
import org.springframework.security.core.Authentication
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.function.Supplier
import javax.annotation.PostConstruct
import kotlin.reflect.KClass

abstract class TwoDeepEntityPage<OUTER : IdentifiableEntity, INNER: IdentifiableEntity>(
    view : String,
    outerClassType: KClass<OUTER>,
    innerClassType: KClass<INNER>,
    innerSupplier: Supplier<INNER>,
    titleSingular: String,
    titlePlural: String,
    description: String,

    private val outerDataSource: EntityPageDataSource<OUTER, Int>,
    innerDataSource: EntityPageDataSource<INNER, Int>,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: ComponentBase,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    entitySourceMapping: Map<String, (INNER?) -> List<String>> =
        mapOf(Nothing::class.simpleName!! to { listOf() }),

    showPermission: PermissionValidator,
    createPermission: PermissionValidator,
    editPermission: PermissionValidator,
    deletePermission: PermissionValidator,
    private val viewPermission: PermissionValidator = showPermission,

    createEnabled: Boolean = false,
    editEnabled: Boolean = false,
    deleteEnabled: Boolean = false,
    importEnabled: Boolean = true,
    exportEnabled: Boolean = true,
    private val viewEnabled: Boolean = true,

    adminMenuCategory: String? = null,
    adminMenuIcon: String = "check_box_outline_blank",
    adminMenuPriority: Int = 1,

    private val outerControlActions: MutableList<ControlAction> = mutableListOf(),
    innerControlActions: MutableList<ControlAction> = mutableListOf(),
    buttonActions: MutableList<ButtonAction> = mutableListOf()
): OneDeepEntityPage<INNER>(
    view,
    innerClassType,
    innerSupplier,
    titleSingular,
    titlePlural,
    description,

    innerDataSource,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    entitySourceMapping,

    showPermission,
    createPermission,
    editPermission,
    deletePermission,

    createEnabled,
    editEnabled,
    deleteEnabled,
    importEnabled,
    exportEnabled,

    adminMenuCategory,
    adminMenuIcon,
    adminMenuPriority,

    innerControlActions,
    buttonActions
) {

    private val outerDescriptor = OverviewBuilder(outerClassType)

    @PostConstruct
    fun initOuter() {
        if (viewEnabled) {
            outerControlActions.add(
                ControlAction(
                    "Megnyitás",
                    "view/{id}",
                    "double_arrow",
                    showPermission,
                    100
                )
            )
        }
    }

    @GetMapping("")
    override fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", outerDescriptor.getColumnsAsJson())
        model.addAttribute("tableData", outerDescriptor.getTableDataAsJson(fetchOuterOverview()))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", toJson(outerControlActions.filter { it.permission.validate(user) }))
        model.addAttribute("buttonActions", buttonActions.filter { it.permission.validate(user) })

        return "overview4"
    }

    @GetMapping("/view/{id}")
    fun view(model: Model, auth: Authentication, @PathVariable id: Int): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (viewPermission.validate(user).not()) {
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", descriptor.getColumnsAsJson())
        model.addAttribute("tableData", descriptor.getTableDataAsJson(filterOverview(user, fetchSublist(id))))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", toJson(controlActions.filter { it.permission.validate(user) }))
        model.addAttribute("buttonActions", buttonActions.filter { it.permission.validate(user) })

        return "overview4"
    }

    open fun fetchOuterOverview(): Iterable<OUTER> {
        return outerDataSource.findAll()
    }

    abstract fun fetchSublist(id: Int): Iterable<INNER>

}
