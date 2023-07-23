package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*
import kotlin.reflect.full.createInstance

@Controller
@RequestMapping("/admin/control/entity")
class EntityViewerController(
    internal val adminMenuService: AdminMenuService,
    internal val auditLog: AuditLogService,
    components: List<ComponentBase>,
    env: Environment
) {

    private val entitySourceMapping: Map<String, (Any?) -> List<String>> =
        mapOf(Nothing::class.simpleName!! to { listOf() })

    private val descriptors = components
        .flatMap { it.entities }
        .filter { it.createInstance().getEntityConfig(env)?.name != null }
        .associate { it.createInstance().getEntityConfig(env)?.name to OverviewBuilder(it.java.kotlin) }

    private val configs = components
        .flatMap { it.entities }
        .mapNotNull { it.createInstance().getEntityConfig(env) }
        .associateBy { it.name }

    private val classes = components
        .flatMap { it.entities }
        .map { it.createInstance() }
        .filter { it.getEntityConfig(env) != null }
        .associateBy { it.getEntityConfig(env)?.name }

    @GetMapping("/{entity}")
    fun edit(@PathVariable entity: String, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        val config = configs[entity]

        if (config == null) {
            model.addAttribute("user", user)
            return "admin404"
        }

        if (config.showPermission.validate(user).not()) {
            model.addAttribute("permission", config.showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, "entity-view", "GET /entity/$entity", config.showPermission.permissionString)
            return "admin403"
        }

        val actualEntity = classes[config.name]
        model.addAttribute("data", actualEntity)
        model.addAttribute("title", config.name)
        model.addAttribute("editMode", true)
        model.addAttribute("duplicateMode", false)
        model.addAttribute("view", config.view.split("/").last())
        model.addAttribute("id", 0)
        model.addAttribute("inputs", descriptors[config.name]?.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("readOnly", false)
        model.addAttribute("entityMode", true)

        return "details"
    }

}
