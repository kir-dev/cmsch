package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.ControlAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import hu.bme.sch.cmsch.util.transaction
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import kotlin.jvm.optionals.getOrNull


@Controller
@RequestMapping("/admin/control/tournament-stage")
@ConditionalOnBean(TournamentComponent::class)
class TournamentStageController(
    private val stageRepository: TournamentStageRepository,
    private val stageService: TournamentStageService,
    private val tournamentRepository: TournamentRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TournamentComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService,
    env: Environment
) : TwoDeepEntityPage<StageGroupDto, TournamentStageEntity>(
    "tournament-stage",
    StageGroupDto::class,
    TournamentStageEntity::class, ::TournamentStageEntity,
    "Kiesési szakasz", "Kiesési szakaszok",
    "A kiesési szakaszok kezelése.",
    transactionManager,
    object : ManualRepository<StageGroupDto, Int>() {
        override fun findAll(): Iterable<StageGroupDto> {
            val stages = stageRepository.findAllAggregated().associateBy { it.tournamentId }
            val tournaments = tournamentRepository.findAll()
            return tournaments.map {
                StageGroupDto(
                    it.id,
                    it.title,
                    it.location,
                    it.participantCount,
                    stages[it.id]?.stageCount?.toInt() ?: 0
                )
            }.sortedByDescending { it.stageCount }
        }
    },
    stageRepository,
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission =   StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    createPermission = StaffPermissions.PERMISSION_CREATE_TOURNAMENTS,
    editPermission =   StaffPermissions.PERMISSION_EDIT_TOURNAMENTS,
    deletePermission = StaffPermissions.PERMISSION_DELETE_TOURNAMENTS,

    viewEnabled = false,
    createEnabled = false,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "lan",

    outerControlActions = mutableListOf(
        ControlAction(
            name = "Torna szakaszainak kezelése",
            endpoint = "view-page/{id}",
            icon = "double_arrow",
            permission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
            order = 100,
            newPage = false,
            usageString = "A kiválasztott torna szakaszainak kezelése"
        )
    ),

    innerControlActions = mutableListOf(
        ControlAction(
            name = "Seedek kezelése",
            endpoint = "seed/{id}",
            icon = "sort_by_alpha",
            permission = StaffPermissions.PERMISSION_SHOW_BRACKETS,
            order = 200,
            newPage = false,
            usageString = "A kiesési szakasz seedjeinek kezelése"
        )
    )
) {
    override fun fetchSublist(id: Int): Iterable<TournamentStageEntity> {
        return stageRepository.findAllByTournamentId(id)
    }

    @GetMapping("/view-page/{id}")
    fun viewPage(model: Model, auth: Authentication, @PathVariable id: Int): String {
        val createKnockoutButtonAction = ButtonAction(
            "Új kiesési szakasz a tornához",
            "create-knockout/$id",
            createPermission,
            99,
            "add_box",
            true
        )
        val createGroupButtonAction = ButtonAction(
            "Új csoport a tornához",
            "create-group/$id",
            createPermission,
            100,
            "add_box",
            true
        )
        val newButtonActions = mutableListOf<ButtonAction>()
        for (buttonAction in buttonActions)
            newButtonActions.add(buttonAction)
        newButtonActions.add(createKnockoutButtonAction)
        newButtonActions.add(createGroupButtonAction)

        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (viewPermission.validate(user).not()) {
            model.addAttribute("permission", viewPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/view/$id", viewPermission.permissionString)
            return "admin403"
        }

        model.addAttribute("title", titlePlural)
        model.addAttribute("titleSingular", titleSingular)
        model.addAttribute("description", description)
        model.addAttribute("view", view)

        model.addAttribute("columnData", descriptor.getColumns())
        val overview = transactionManager.transaction(readOnly = true) { filterOverview(user, fetchSublist(id)) }
        model.addAttribute("tableData", descriptor.getTableData(overview))

        model.addAttribute("user", user)
        model.addAttribute("controlActions", controlActions.filter { it.permission.validate(user) })
        model.addAttribute("allControlActions", controlActions)
        model.addAttribute("buttonActions", newButtonActions.filter { it.permission.validate(user) })

        attachPermissionInfo(model)

        return "overview4"
    }

    @GetMapping(value = ["/create-knockout/{tournamentId}", "/create-group/{tournamentId}"])
    fun createStagePage(model: Model, auth: Authentication, @PathVariable tournamentId: Int): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (editPermission.validate(user).not()) {
            model.addAttribute("permission", editPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/create/$tournamentId", showPermission.permissionString)
            return "admin403"
        }

        if (!editEnabled)
            return "redirect:/admin/control/$view/"

        val entity = TournamentStageEntity(tournamentId =  tournamentId)

        val actualEntity = onPreEdit(entity)
        model.addAttribute("data", actualEntity)
        if (!editPermissionCheck(user, actualEntity, null)) {
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/create/$tournamentId",
                "editPermissionCheck() validation")
            return "admin403"
        }

        model.addAttribute("title", titleSingular)
        model.addAttribute("editMode", false)
        model.addAttribute("duplicateMode", false)
        model.addAttribute("view", view)
        model.addAttribute("inputs", descriptor.getInputs())
        model.addAttribute("mappings", entitySourceMapping)
        model.addAttribute("user", user)
        model.addAttribute("readOnly", false)
        model.addAttribute("entityMode", false)

        onDetailsView(user, model)
        return "details"
    }

    @Override
    @PostMapping("/create", headers =  ["Referer"])
    fun create(@ModelAttribute(binding = false) dto: TournamentStageEntity,
               @RequestParam(required = false) file0: MultipartFile?,
               @RequestParam(required = false) file1: MultipartFile?,
               model: Model,
               auth: Authentication,
               @RequestHeader("Referer") referer: String,
    ): String {
        val toCreate = referer.substringAfterLast("/create-").split("/")
        val stageType = toCreate.getOrNull(0)
            ?.let { when (it) {
                "knockout" -> StageType.KNOCKOUT
                "group" -> StageType.GROUP_STAGE
                else -> return "redirect:/admin/control/$view"
            }} ?: return "redirect:/admin/control/$view"
        val tournamentId = toCreate.getOrNull(1)?.toIntOrNull()
            ?: return "redirect:/admin/control/$view"
//        val tournamentId = referer.substringAfterLast("/create/").toIntOrNull()
//            ?: return "redirect:/admin/control/$view"
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if (createPermission.validate(user).not()) {
            model.addAttribute("permission", createPermission.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /$view/create", createPermission.permissionString)
            return "admin403"
        }

        if (!editPermissionCheck(user, null, dto)) {
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /$view/create", "editPermissionCheck() validation")
            return "admin403"
        }

        dto.tournamentId = tournamentId
        dto.type = stageType
        val newValues = StringBuilder("entity new value: ")
        updateEntity(descriptor, user, dto, dto, newValues, false, file0, false, file1)
        dto.id = 0
        if (onEntityPreSave(dto, auth)) {
            auditLog.create(user, component.component, newValues.toString())
            transactionManager.transaction(readOnly = false, isolation = TransactionDefinition.ISOLATION_READ_COMMITTED) {
                dataSource.save(dto)
            }
        }
        onEntityChanged(dto)
        return "redirect:/admin/control/$view"
    }


    @GetMapping("/seed/{id}")
    fun seedPage(model: Model, auth: Authentication, @PathVariable id: Int): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(!StaffPermissions.PERMISSION_SHOW_BRACKETS.validate(user) ) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_GENERATE_BRACKETS.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "GET /$view/seed/$id", StaffPermissions.PERMISSION_GENERATE_BRACKETS.permissionString)
            return "admin403"
        }
        val stage = stageRepository.findById(id).getOrNull()
            ?: return "redirect:/admin/control/$view"
        val readOnly = !StaffPermissions.PERMISSION_SET_SEEDS.validate(user) || stage.status >= StageStatus.SET
        val teams = stageService.getParticipants(id).sortedBy { it.initialSeed }
        val tournament = tournamentRepository.findById(stage.tournamentId).getOrNull()
            ?: return "redirect:/admin/control/$view"
        model.addAttribute("title", "Kiesési szakasz seedek")
        model.addAttribute("view", view)
        model.addAttribute("readOnly", readOnly)
        model.addAttribute("entityMode", false)
        model.addAttribute("tournamentTitle", tournament.title)
        model.addAttribute("stage", stage)
        model.addAttribute("teams", teams)

        return "seedSettings"
    }


    @PostMapping("/seed/{id}")
    fun editSeed(
        @PathVariable id: Int,
        @RequestParam allRequestParams: Map<String, String>,
        model: Model,
        auth: Authentication
    ): String {
        val user = auth.getUser()
        adminMenuService.addPartsForMenu(user, model)
        if(!StaffPermissions.PERMISSION_SET_SEEDS.validate(user) ) {
            model.addAttribute("permission", StaffPermissions.PERMISSION_SET_SEEDS.permissionString)
            model.addAttribute("user", user)
            auditLog.admin403(user, component.component, "POST /$view/seed/$id", StaffPermissions.PERMISSION_SET_SEEDS.permissionString)
            return "admin403"
        }
        val stage = stageRepository.findById(id).getOrNull()
            ?: return "redirect:/admin/control/$view"
        if (stage.status >= StageStatus.SET) {
            model.addAttribute("error", "A szakasz már be lett állítva, nem lehet módosítani a seedeket.")
            return "redirect:/admin/control/$view/seed/${id}"
        }

        val dto = mutableListOf<StageResultDto>()
        var i = 0
        while (allRequestParams["id_$i"] != null && allRequestParams["order_$i"] != null) {
            val teamId = allRequestParams["id_$i"]!!.toInt()
            val teamName = allRequestParams["name_$i"] ?: ""
            val initialSeed = allRequestParams["order_$i"]?.toIntOrNull()?: 0
            val highlighted = allRequestParams["highlighted_$i"] != null && allRequestParams["highlighted_$i"] != "off"
            dto.add(StageResultDto(
                teamId, teamName,
                stage.id,
                highlighted = highlighted,
                initialSeed = initialSeed + 1 // Adjusting seed to be positive
            ))
            i++
        }
        val stageStatus: StageStatus = allRequestParams["stageStatus"]!!.let { when (it) {
            "CREATED" -> StageStatus.CREATED
            "SET" -> StageStatus.SET
            else -> return "error"
        }}

        val updatedSeeds = dto.map { it.initialSeed }.toSet()
        if (updatedSeeds.size != dto.size) {
            model.addAttribute("error", "Minden seednek egyedinek kell lennie.")
            return "redirect:/admin/control/$view/seed/${id}"
        }
        val stageEntity = stage
        if (onEntityPreSave(stageEntity, auth)) {
            transactionManager.transaction(readOnly = false, isolation = TransactionDefinition.ISOLATION_READ_COMMITTED) {
                stageService.setInitialSeeds(stageEntity, dto, user)
                stageEntity.seeds=stageService.setSeeds(stageEntity)
                stageEntity.status= stageStatus
                stageRepository.save(stageEntity)
                if (stageStatus == StageStatus.SET)
                    stageService.onSeedsFinalized(stageEntity)
                stageService.calculateTeamsFromSeeds(stageEntity)
            }
            auditLog.edit(user, component.component+"seed", dto.toString())
        } else {
            model.addAttribute("error", "A szakasz nem lett frissítve.")
        }
        onEntityChanged(stageEntity)
        return "redirect:/admin/control/$view/seed/${id}"
    }

    override fun onEntityPreSave(entity: TournamentStageEntity, auth: Authentication): Boolean {
        if (entity.type == StageType.KNOCKOUT) {
            entity.participants = stageService.transferTeamsForStage(entity)
            stageService.createMatchesForStage(entity)
            entity.seeds = stageService.setSeeds(entity)
            stageService.calculateTeamsFromSeeds(entity)
        }
        return super.onEntityPreSave(entity, auth)
    }

    override fun onEntityDeleted(entity: TournamentStageEntity) {
        stageService.deleteMatchesForStage(entity)
        super.onEntityDeleted(entity)
    }
}
