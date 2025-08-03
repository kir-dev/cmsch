package hu.bme.sch.cmsch.component.script

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.component.script.sandbox.ScriptArtifact
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.Arrays
import kotlin.jvm.optionals.getOrNull


@Controller
@ConditionalOnBean(ScriptComponent::class)
class ScriptsLogsDashboard(
    private val adminMenuService: AdminMenuService,
    private val auditLog: AuditLogService,
    private val component: ScriptComponent,
    private val platformTransactionManager: PlatformTransactionManager,
    private val scriptResultRepository: ScriptResultRepository,
    private val scriptRepository: ScriptRepository,
) {

    private final val showPermission = StaffPermissions.PERMISSION_SHOW_SCRIPTS
    private final val editPermission = StaffPermissions.PERMISSION_EDIT_SCRIPTS
    private final val view = "script-logs"
    private final val artifactReader = jacksonObjectMapper().readerFor(object : TypeReference<List<ScriptArtifact>>() {})

    @GetMapping("/admin/control/script-logs/{scriptResultId}")
    fun viewScript(model: Model, @PathVariable scriptResultId: Int, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            return "admin403"
        }

        val scriptEntity = platformTransactionManager.transaction(readOnly = true) {
            scriptResultRepository.findById(scriptResultId).getOrNull()?.let {
                scriptRepository.findById(it.scriptId).getOrNull()
            }
        }

        model.addAttribute("user", user)
        model.addAttribute("scriptName", scriptEntity?.name ?: "Nem található")
        model.addAttribute("scriptResultId", scriptResultId)
        model.addAttribute("enableEdit", editPermission.validate(user))

        return "script-logs"
    }

    @ResponseBody
    @JsonView(Edit::class)
    @GetMapping("/api/script-logs/{scriptResultId}")
    fun fetchScriptLogs(auth: Authentication?, @PathVariable scriptResultId: Int): ScriptResultEntity {
        val user = auth?.getUser()
            ?: error("Unauthorized")
        if (showPermission.validate(user).not()) {
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            error("Unauthorized")
        }

        val scriptEntity = platformTransactionManager.transaction(readOnly = true) {
            scriptResultRepository.findById(scriptResultId).getOrNull()
                ?: throw IllegalStateException("No script found")
        }
        return scriptEntity
    }

    @GetMapping("/admin/control/script-logs/{scriptResultId}/artifact/{artifactId}")
    fun viewScript(
        model: Model,
        @PathVariable scriptResultId: Int,
        @PathVariable artifactId: Int,
        auth: Authentication,
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            return "admin403"
        }

        val (scriptResultEntity, scriptEntity) = platformTransactionManager.transaction(readOnly = true) {
            scriptResultRepository.findById(scriptResultId).getOrNull()?.let {
                return@transaction it to scriptRepository.findById(it.scriptId).getOrNull()
            }
            return@transaction Pair(null, null)
        }

        model.addAttribute("user", user)
        model.addAttribute("scriptName", scriptEntity?.name ?: "Nem található")
        model.addAttribute("scriptResultId", scriptResultId)

        if (scriptResultEntity != null) {
            val artifact = artifactReader.readValue<List<ScriptArtifact>>(scriptResultEntity.artifacts).getOrNull(artifactId)
            model.addAttribute("mimeType", artifact?.type)
            model.addAttribute("name", artifact?.artifactName)

            if (artifact?.type?.lowercase()?.trim() == "text/csv") {
                val csv = loadCsvAsList(artifact.content)
                if (csv.isNotEmpty()) {
                    model.addAttribute("contentTableHeader", csv.first())
                    model.addAttribute("contentTable", csv.drop(1))
                } else {
                    model.addAttribute("contentTableHeader", listOf<String>())
                    model.addAttribute("contentTable", listOf<List<String>>())
                }
            } else {
                model.addAttribute("contentText", artifact?.content)
            }
        }

        return "script-artifacts"
    }

    final fun loadCsvAsList(csv: String): List<List<String>> {
        val mapper = CsvMapper()
        mapper.enable(CsvParser.Feature.WRAP_AS_ARRAY)

        val schema = CsvSchema.emptySchema()

        val it: MappingIterator<Array<String>> = mapper.readerFor(Array<String>::class.java)
            .with(schema)
            .readValues(csv)

        val rows: MutableList<List<String>> = mutableListOf()
        while (it.hasNext()) {
            val row: Array<String> = it.next()!!
            rows.add(row.toList())
        }

        return rows
    }

}