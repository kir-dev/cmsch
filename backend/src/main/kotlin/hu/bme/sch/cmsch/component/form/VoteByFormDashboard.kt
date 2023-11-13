package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hu.bme.sch.cmsch.admin.dashboard.*
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping


private const val VIEW = "form-vote"

@Controller
@RequestMapping("/admin/control/$VIEW")
@ConditionalOnBean(FormComponent::class)
class VoteByFormDashboard(
    private val adminMenuService: AdminMenuService,
    formComponent: FormComponent,
    auditLogService: AuditLogService,
    private val formService: FormService,
) : DashboardPage(
    view = VIEW,
    title = "Szavazások eredménye",
    description = "",
    wide = false,
    adminMenuService = adminMenuService,
    component = formComponent,
    auditLog = auditLogService,
    showPermission = StaffPermissions.PERMISSION_SHOW_FORM_RESULTS,
    adminMenuCategory = null,
    ignoreFromMenu = true
) {

    private val objectMapper = jacksonObjectMapper()
    val formStructReader = objectMapper.readerFor(object : TypeReference<List<FormElement>>() {})
    val submittedFieldsReader = objectMapper.readerFor(object : TypeReference<Map<String, String>>() {})

    private val permissionCard = DashboardPermissionCard(
        1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = wide
    )

    private val error404 = DashboardCard(
        id = 2,
        wide = wide,
        title = "Űrlap nem található",
        description = "Ha menün keresztül jutottál ide, akkor értesíts egy fejlesztőt",
        content = listOf("Űrlap nem található ezzel az azonosítóval"),
    )

    private val errorNoVotesFound = DashboardCard(
        id = 2,
        wide = wide,
        title = "Ebben az űrlapban nics szavazás",
        description = "Csak azoknál az űrlapoknál használható ez a menü, amiben van szavazás (VOTE) mező",
        content = listOf("Ebben az űrlapban nics szavazás"),
    )

    @GetMapping("/{id}")
    fun viewForm(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /${view}", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", title)
        model.addAttribute("description", description)
        model.addAttribute("view", view)
        model.addAttribute("wide", wide)
        model.addAttribute("components", getFormComponents(user, id))
        model.addAttribute("user", user)

        return "dashboard"
    }

    private fun getFormComponents(adminUser: CmschUser, formId: Int): List<DashboardComponent> {
        val form = formService.getForm(formId)
            ?: return listOf(permissionCard, error404)

        val formStruct = formStructReader.readValue<List<FormElement>>(form.formJson)

        val votes = formStruct.filter { it.type == FormElementType.VOTE || it.type == FormElementType.SELECT }

        if (votes.isEmpty())
            return listOf(permissionCard, errorNoVotesFound)

        return votes.map { getDetailsValidator(form, it) }
    }

    private fun getDetailsValidator(form: FormEntity, voteField: FormElement): DashboardComponent {
        val submissions = formService.getSubmissions(form)
        val stats = submissions
            .asSequence()
            .map { submittedFieldsReader.readValue<Map<String, String>>(it.submission)[voteField.fieldName] ?: "<<n/a>>" }
            .groupBy { it }
            .map { listOf(it.key, it.value.size.toString()) }
            .toList()
            .sortedBy { it[1] }
            .toList()

        return DashboardTableCard(
            2,
            "${form.name} / ${voteField.label}",
            "Szavazatok a(z) '${voteField.fieldName}' mezőre. Amire nem érkezett szavazat, az nincs beleszámolva. Összes szavazat: ${submissions.size}",
            listOf("Érték", "Szavazatok"),
            stats,
            false,
            exportable = false
        )
    }


    override fun getComponents(user: CmschUser): List<DashboardComponent> {
        return listOf(permissionCard, error404)
    }

}
