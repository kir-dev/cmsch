package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.extending.FormSubmissionListener
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.TimeService
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import kotlin.jvm.optionals.getOrNull

const val FORM_MASTER_FILL = "form-master-fill"

private const val OWNER_ID_FIELD = "__ownerId"
private const val OWNER_NAME_FIELD = "__ownerName"
private const val OWNER_EMAIL_FIELD = "__ownerEmail"

@Controller
@RequestMapping("/admin/control/$FORM_MASTER_FILL")
@ConditionalOnBean(FormComponent::class)
class FormMasterFillDashboard(
    private val adminMenuService: AdminMenuService,
    applicationComponent: FormComponent,
    auditLogService: AuditLogService,
    private val platformTransactionManager: PlatformTransactionManager,
    private val formRepository: FormRepository,
    private val responseRepository: ResponseRepository,
    private val listeners: MutableList<out FormSubmissionListener>,
    private val timeService: TimeService,
) : DashboardPage(
    view = FORM_MASTER_FILL,
    title = "Űrlap kitöltése",
    description = "Ezzel a menüvel ki lehet tölteni bárki nevében árlapokat. Hasznos akkor, hogyha valamit úgy kell felvenni, hogy az ellenőrézsek ne fussanak le rajta.",
    wide = false,
    adminMenuService = adminMenuService,
    component = applicationComponent,
    auditLog = auditLogService,

    adminMenuCategory = "",
    showPermission = StaffPermissions.PERMISSION_EDIT_FORM,
    adminMenuIcon = "edit_note",
    adminMenuPriority = 1
) {

    private val permissionCard = DashboardPermissionCard(
        id = 1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    private val formNotFound = DashboardCard(
        id = 2,
        wide = wide,
        title = "Űrlap nem található!",
        description = "",
        content = listOf("Ha egy menüből jutottál ide, jelezd a fejlesztőknek!")
    )

    private val objectMapper = jacksonObjectMapper()
    private val formStructReader = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})

    override fun getComponents(user: CmschUser, requestParams: Map<String, String>): List<DashboardComponent> {
        return listOf(
            permissionCard,
            formNotFound
        )
    }

    @GetMapping("/form/{formId}")
    fun viewForm(
        model: Model,
        auth: Authentication,
        @RequestParam requestParams: Map<String, String>,
        @PathVariable formId: Int
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", getFormComponents(user, requestParams, formId))
        model.addAttribute("user", user)
        model.addAttribute("card", requestParams.getOrDefault("card", "-1").toIntOrNull())
        model.addAttribute("message", requestParams.getOrDefault("message", ""))

        return "dashboard"
    }

    fun getFormComponents(user: CmschUser, requestParams: Map<String, String>, formId: Int): List<DashboardComponent> {
        return listOf(
            permissionCard,
            getForm(formId)
        )
    }

    private fun getForm(formId: Int): DashboardComponent {
        val form = platformTransactionManager.transaction(readOnly = true) { formRepository.findById(formId).getOrNull() }
            ?: return formNotFound

        val elements = try {
            formStructReader.readValue<List<FormElement>>(form.formJson)
        } catch (e: JacksonException) {
            listOf()
        }.map { element ->
            if (!element.type.rendersOnServerSide) {
                element.type = FormElementType.TEXT
            }
            element
        }

        val formElements = mutableListOf<FormElement>()
        formElements.addAll(listOf(
            FormElement(
                OWNER_ID_FIELD, "Tulajdonos ID-je", FormElementType.TEXT,
                ".*", "", "",
                if (form.ownerIsGroup) "Csoport ID-je (opcionális)" else "Felhasználó ID-je (opcionális)",
                required = false, permanent = false, defaultValue = ""
            ),
            FormElement(
                OWNER_NAME_FIELD, "Tulajdonos neve", FormElementType.TEXT,
                ".*", "", "",
                if (form.ownerIsGroup) "Csoport neve (opcionális)" else "Felhasználó neve (opcionális)",
                required = false, permanent = false, defaultValue = ""
            ),
        ))
        if (!form.ownerIsGroup) {
            FormElement(
                OWNER_EMAIL_FIELD, "Felhasználó email címe", FormElementType.TEXT,
                ".*", "", "",
                "(opcionális)",
                required = false, permanent = false, defaultValue = ""
            )
        }
        formElements.addAll(elements)

        return DashboardFormCard(
            id = 2,
            wide = false,
            title = form.name,
            description = "",
            content = formElements,
            buttonCaption = "Beküldés",
            buttonIcon = "send",
            action = "form/${formId}/send",
            method = "post"
        )
    }

    @PostMapping("/form/{formId}/send")
    fun upgradeCsv(
        auth: Authentication,
        @RequestParam allRequestParams: Map<String, String>,
        @PathVariable formId: Int
    ): String {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }

        platformTransactionManager.transaction(readOnly = false) {
            val form = formRepository.findById(formId).getOrNull()
                ?: return dashboardPage(FORM_MASTER_FILL, -1, "Űrlap nem található!")
            val formStruct = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})
                .readValue<List<FormElement>>(form.formJson)

            val submission = mutableMapOf<String, String>()

            for (field in formStruct) {
                var value = allRequestParams[field.fieldName] ?: ""

                if (field.type == FormElementType.CHECKBOX) {
                    value = (value.equals("on", ignoreCase = true)).toString()
                }

                submission[field.fieldName] = value
            }

            val responseEntity = ResponseEntity(
                submitterUserId = if (form.ownerIsGroup) null else allRequestParams[OWNER_ID_FIELD]?.toIntOrNull(),
                submitterUserName = if (form.ownerIsGroup) "" else (allRequestParams[OWNER_NAME_FIELD] ?: ""),
                submitterGroupId = if (form.ownerIsGroup) allRequestParams[OWNER_ID_FIELD]?.toIntOrNull() else null,
                submitterGroupName = if (form.ownerIsGroup) (allRequestParams[OWNER_NAME_FIELD] ?: "") else "",
                formId = form.id,
                creationDate = timeService.getTime(),
                accepted = false,
                rejected = false,
                email = allRequestParams[OWNER_EMAIL_FIELD] ?: "",
                submission = objectMapper.writeValueAsString(submission),
                line = (responseRepository.findTop1ByFormIdOrderByLineDesc(form.id).firstOrNull()?.line ?: 0) + 1
            )

            responseRepository.save(responseEntity)
            listeners.forEach { it.onFormSubmitted(user, form, responseEntity) }
        }


        return dashboardPage("$FORM_MASTER_FILL/form/$formId", 2, "Beküldés sikeres!")
    }

}