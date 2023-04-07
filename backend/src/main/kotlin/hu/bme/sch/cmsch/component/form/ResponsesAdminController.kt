package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/admin/control/signup-responses")
@ConditionalOnBean(FormComponent::class)
class ResponsesAdminController(
    private val responseRepository: ResponseRepository,
    private val formRepository: FormRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: FormComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : TwoDeepEntityPage<FormVirtualEntity, ResponseEntity>(
    "signup-responses",
    FormVirtualEntity::class,
    ResponseEntity::class, ::ResponseEntity,
    "Kitöltés", "Kitöltések",
    "Kitöltések formonként csoportosítva",

    object : ManualRepository<FormVirtualEntity, Int>() {
        override fun findAll(): Iterable<FormVirtualEntity> {
            return responseRepository.findAll()
                .groupBy { it.formId }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .mapNotNull { it ->
                    formRepository.findById(it[0].formId)
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

    },
    responseRepository,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS,
    deletePermission = ImplicitPermissions.PERMISSION_NOBODY,

    createEnabled = false,
    editEnabled   = true,
    deleteEnabled = false,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "inbox",
    adminMenuPriority = 2,

    outerControlActions = mutableListOf(
        ControlAction(
            "Json Export",
            "export/json/{id}",
            "data_object",
            StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS,
            10,
            true
        ),
        ControlAction(
            "CSV Export",
            "export/json/{id}",
            "save",
            StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS,
            20,
            true
        )
    )
) {

    private val exportPermission = StaffPermissions.PERMISSION_EDIT_SIGNUP_RESULTS

    override fun fetchSublist(id: Int): Iterable<ResponseEntity> {
        return responseRepository.findAllByFormId(id)
    }

    @ResponseBody
    @GetMapping(value = ["/export/csv/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportCsv(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (exportPermission.validate(user).not()) {
            return "403"
        }

        val objReader = objectMapper.readerFor(object : TypeReference<Map<String, Any>>() {})
        val entries = responseRepository.findAllByFormId(id)
            .map { objReader.readValue<Map<String, Any>>(it.submission) }
            .map { it.values }
            .toList()

        val headers = objReader.readValue<Map<String, Any>>(responseRepository.findAllByFormId(id).firstOrNull()?.submission ?: "{}")
            .keys
            .joinToString(",")
        val result = CsvMapper().writeValueAsString(entries)

        return headers + "\n" + result
    }

    @ResponseBody
    @GetMapping(value = ["/export/json/{id}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun exportJson(@PathVariable id: Int, auth: Authentication): String {
        val user = auth.getUser()
        if (exportPermission.validate(user).not()) {
            return "403"
        }

        val entries = responseRepository.findAllByFormId(id).joinToString(",") { it.submission }

        return "[${entries}]"
    }

}
