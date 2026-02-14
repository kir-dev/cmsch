package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.admin.dashboard.DashboardCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardComponent
import hu.bme.sch.cmsch.admin.dashboard.DashboardFormCard
import hu.bme.sch.cmsch.admin.dashboard.DashboardPermissionCard
import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.form.FormElementType
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper
import kotlin.jvm.optionals.getOrNull

@Controller
@RequestMapping("/admin/control/community")
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesController(
    val tinderService: TinderService,
    val organizationService: OrganizationService,
    repo: CommunityRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<CommunityEntity>(
    "community",
    CommunityEntity::class, ::CommunityEntity,
    "Kör", "Körök",
    "Körök kezelése",

    transactionManager,
    repo,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
    createPermission = StaffPermissions.PERMISSION_CREATE_COMMUNITIES,
    editPermission =   StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
    deletePermission = StaffPermissions.PERMISSION_DELETE_COMMUNITIES,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = true,
    exportEnabled = true,

    adminMenuIcon = "supervised_user_circle",
    adminMenuPriority = 1,

    searchSettings = calculateSearchSettings<CommunityEntity>(false),

    controlActions = mutableListOf(
        ControlAction(
            name = "Válaszok megtekintése",
            endpoint = "tinder/show/{id}",
            icon = "label",
            permission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
            order = 250,
            usageString = "Tinder válaszok megtekintése",
        ),
        ControlAction(
            name = "Válaszok szerkesztése",
            endpoint = "tinder/edit/{id}",
            icon = "rate_review",
            permission = StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
            order = 260,
            usageString = "Tinder válaszok szerkesztése",
        )
    ),
){

    private val showDesc = "Tinder válaszok megtekintése"
    private val editDesc = "Tinder válaszok szerkesztése"

    private val permissionCard = DashboardPermissionCard(
        id = 1,
        permission = showPermission.permissionString,
        description = "Ez a jog szükséges ennek az oldalnak az olvasásához.",
        wide = false
    )

    private val reader = objectMapper.readerFor(object: TypeReference<Map<Int, String>>(){})

    @GetMapping("/tinder/show/{id}")
    fun showTinderAnswers(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (showPermission.validate(user).not()) {
            model.addAttribute("permission", showPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/tinder/show/$id", showPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", showDesc)
        model.addAttribute("description", "Körhöz tartozó tinder válaszok megtekintése")
        model.addAttribute("view", "community/tinder/show/$id")
        model.addAttribute("user", user)
        model.addAttribute("wide", false)
        model.addAttribute("components", getTinderShowComponents(id))
        model.addAttribute("card", -1)
        model.addAttribute("message", "")

        return "dashboard"
    }


    @GetMapping("/tinder/edit/{id}")
    fun editTinderAnswers(@PathVariable id: Int, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/tinder/edit/$id", editPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", editDesc)
        model.addAttribute("description", "Körhöz tartozó tinder válaszok szerkesztése")
        model.addAttribute("view", "community/tinder/edit/$id")
        model.addAttribute("user", user)
        model.addAttribute("wide", false)
        model.addAttribute("components", getTinderEditComponents(id))
        model.addAttribute("card", -1)
        model.addAttribute("message", "")

        return "dashboard"
    }


    @PostMapping("/tinder/edit/{id}/")
    fun editTinderAnswers(@PathVariable id: Int, @RequestParam params: Map<String, String>, model: Model, auth: Authentication): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /$view/tinder/edit/$id", editPermission.permissionString)
            return "admin403"
        }

        return tinderService.updateCommunityAnswer(user, id, params)
    }


    private fun getTinderEditComponents(communityId: Int): List<DashboardComponent>{
        val communityName = transactionManager.transaction(readOnly = true) { dataSource.findById(communityId).getOrNull()?.name }
            ?: return listOf(
                permissionCard,
                DashboardCard(
                    id = 2,
                    wide = false,
                    title = "Közösség nem található",
                    description = "A megadott azonosítóval (id = $communityId) nincs közösség az adatbázisban.",
                    content = listOf()
                )
            )
        return listOf(
            permissionCard,
            DashboardCard(
                id = 2,
                wide = false,
                title = communityName,
                description = "$communityName tinder válaszainak szerkesztése",
                content = listOf()
            ),
            getTinderEditComponent(communityId)
        )
    }

    private fun getTinderEditComponent(communityId: Int): DashboardComponent {
        val answerEntity = transactionManager.transaction(readOnly = true) { tinderService.getAnswerForCommunity(communityId).getOrNull() }
            .let {
                reader.readValue<Map<Int, String>>(it?.answers)
            }
            ?: return DashboardFormCard(
                id = 3,
                wide = false,
                title = "Nincsenek válaszok",
                content = listOf(),
                buttonCaption = "",
                method = "",
                action = "",
                buttonIcon = ""
            )

        val questions = transactionManager.transaction(readOnly = true) { tinderService.getAllQuestions() }

        val formElements = mutableListOf<FormElement>()
        for (question in questions) {
            formElements.add(FormElement(
                fieldName = "question_${question.id}",
                label = question.question,
                type = FormElementType.SELECT,
                values = ","+question.answerOptions,
                defaultValue = answerEntity[question.id] ?: ""
            ))
        }

        return DashboardFormCard(
            id = 3,
            wide = false,
            title = editDesc,
            content = formElements,
            buttonCaption = "Mentés",
            method = "post",
            action = "",
            buttonIcon = "save"
        )
    }


    private fun getTinderShowComponents(communityId: Int): List<DashboardComponent>{
        val communityName = transactionManager.transaction(readOnly = true) { dataSource.findById(communityId).getOrNull()?.name }
            ?: return listOf(
                permissionCard,
                DashboardCard(
                    id = 2,
                    wide = false,
                    title = "Közösség nem található",
                    description = "A megadott azonosítóval (id = $communityId) nincs közösség az adatbázisban.",
                    content = listOf()
                )
            )
        return listOf(
            permissionCard,
            DashboardCard(
                id = 2,
                wide = false,
                title = communityName,
                description = "$communityName tinder válaszainak szerkesztése",
                content = listOf()
            ),
            getTinderShowComponent(communityId)
        )
    }

    private fun getTinderShowComponent(communityId: Int): DashboardComponent {
        val answerEntity = transactionManager.transaction(readOnly = true) { tinderService.getAnswerForCommunity(communityId).getOrNull() }
            .let {
                reader.readValue<Map<Int, String>>(it?.answers)
            }
            ?: return DashboardFormCard(
                id = 3,
                wide = false,
                title = "Nincsenek válaszok",
                content = listOf(),
                buttonCaption = "",
                method = "",
                action = "",
                buttonIcon = ""
            )
        val questions = transactionManager.transaction(readOnly = true) { tinderService.getAllQuestions() }

        val formElements = mutableListOf<FormElement>()
        for (i in 0..<questions.size) {
            val questionText = questions[i].question
            formElements.add(FormElement(
                fieldName = "question_$i",
                label = questionText,
                type = FormElementType.INFO_BOX,
                values = answerEntity[questions[i].id] ?: "Nincs válasz"
            ))
        }

        return DashboardFormCard(
            id = 3,
            wide = false,
            title = showDesc,
            content = formElements,
            buttonCaption = "",
            method = "",
            action = "",
            buttonIcon = ""
        )
    }

    override fun onEntityChanged(entity: CommunityEntity) {
        tinderService.ensureCommunityAnswer(entity)
    }

    override fun onEntityPreSave(entity: CommunityEntity, auth: Authentication): Boolean {
        val resName = organizationService.getOrganizationById(entity.resortId)            .map { it.name }
            .getOrNull() ?: return false
        entity.resortName = resName
        return true
    }

}
