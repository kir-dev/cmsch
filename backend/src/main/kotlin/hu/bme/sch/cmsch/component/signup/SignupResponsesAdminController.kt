package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import hu.bme.sch.cmsch.admin.INPUT_TYPE_FILE
import hu.bme.sch.cmsch.admin.INTERPRETER_INHERIT
import hu.bme.sch.cmsch.admin.OverviewBuilder
import hu.bme.sch.cmsch.controller.CONTROL_MODE_EDIT
import hu.bme.sch.cmsch.controller.CONTROL_MODE_VIEW_EXPORT2
import hu.bme.sch.cmsch.controller.INVALID_ID_ERROR
import hu.bme.sch.cmsch.service.AdminMenuEntry
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.getUserFromDatabase
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import kotlin.reflect.KMutableProperty1

@Controller
@RequestMapping("/admin/control/signup-responses")
@ConditionalOnBean(SignupComponent::class)
class SignupResponsesAdminController(
    private val signupResponseRepository: SignupResponseRepository,
    private val signupFormRepository: SignupFormRepository,
    private val clock: TimeService,
    private val adminMenuService: AdminMenuService,
    private val objectMapper: ObjectMapper
) {

    private val view = "signup-responses"
    private val titleSingular = "Kitöltés"
    private val titlePlural = "Kitöltések"
    private val description = "Kitöltések formonként csoportosítva"
    private val permissionControl = PERMISSION_EDIT_SIGNUP_RESULTS

    private val entitySourceMapping: Map<String, (SignupResponseEntity) -> List<String>> =
            mapOf(Nothing::class.simpleName!! to { listOf() })

    private val overviewDescriptor = OverviewBuilder(FormVirtualEntity::class)
    private val submittedDescriptor = OverviewBuilder(SignupResponseEntity::class)

    @PostConstruct
    fun init() {
        adminMenuService.registerEntry(
            SignupComponent::class.simpleName!!, AdminMenuEntry(
                titlePlural,
                "inbox",
                "/admin/control/${view}",
                2,
                permissionControl
            )
        )
    }

    @GetMapping("")
    fun view(model: Model, auth: Authentication): String {
        val user = auth.getUser()
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
        model.addAttribute("controlMode", CONTROL_MODE_VIEW_EXPORT2)

        return "overview"
    }

    private fun fetchOverview(): List<FormVirtualEntity> {
        return signupResponseRepository.findAll()
            .groupBy { it.formId }
            .map { it.value }
            .filter { it.isNotEmpty() }
            .mapNotNull { it ->
                signupFormRepository.findById(it[0].formId)
                    .map { form ->
                        FormVirtualEntity(
                            form.id,
                            form.name,
                            form.submissionLimit,
                            it.size,
                            it.count { it.accepted },
                            it.count { it.rejected },
                            it.count { it.detailsValidated },
                        )
                    }.orElse(null)
            }
    }

    @GetMapping("/view/{id}")
    fun viewAll(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
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
        model.addAttribute("rows", signupResponseRepository.findAllByFormId(id))
        model.addAttribute("user", user)
        model.addAttribute("controlMode", CONTROL_MODE_EDIT)

        return "overview"
    }

    @ResponseBody
    @GetMapping(value = ["/export/csv/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportCsv(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            return "403"
        }

        val objReader = objectMapper.readerFor(object : TypeReference<Map<String, Any>>() {})
        val entries = signupResponseRepository.findAllByFormId(id)
            .map { objReader.readValue<Map<String, Any>>(it.submission) }
            .map { it.values }
            .toList()

        val headers = objReader.readValue<Map<String, Any>>(signupResponseRepository.findAllByFormId(id).firstOrNull()?.submission ?: "{}")
            .keys
            .joinToString(",")
        val result = CsvMapper().writeValueAsString(entries)

        return headers + "\n" + result
    }

    @ResponseBody
    @GetMapping(value = ["/export/json/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportJson(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (permissionControl.validate(user).not()) {
            return "403"
        }

        val entries = signupResponseRepository.findAllByFormId(id).joinToString(",") { it.submission }

        return "[${entries}]"
    }

    @GetMapping("/edit/{id}")
    fun edit(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
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

        val entity = signupResponseRepository.findById(id)
        if (entity.isEmpty) {
            model.addAttribute("error", INVALID_ID_ERROR)
        } else {
            model.addAttribute("data", entity.orElseThrow())
        }
        return "details"
    }

    @PostMapping("/edit/{id}")
    fun edit(@PathVariable id: Int,
             @ModelAttribute(binding = false) dto: SignupResponseEntity,
             model: Model,
             auth: Authentication
    ): String {
        val user = auth.getUserFromDatabase()
        if (permissionControl.validate(user).not()) {
            model.addAttribute("permission", permissionControl.permissionString)
            model.addAttribute("user", user)
            return "admin403"
        }

        val entity = signupResponseRepository.findById(id)
        if (entity.isEmpty) {
            return "redirect:/admin/control/$view/edit/$id"
        }

        val date = clock.getTimeInSeconds()
        val response = entity.get()
        updateEntity(submittedDescriptor, response, dto)
        response.lastUpdatedDate = date
        response.id = id
        signupResponseRepository.save(response)
        return "redirect:/admin/control/$view"
    }

    private fun updateEntity(
        descriptor: OverviewBuilder,
        entity: SignupResponseEntity,
        dto: SignupResponseEntity
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
