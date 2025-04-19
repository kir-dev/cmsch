package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ImportService
import hu.bme.sch.cmsch.service.StaffPermissions
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping


@Controller
@RequestMapping("/admin/control/knockout-stage")
@ConditionalOnBean(TournamentComponent::class)
class KnockoutStageController(
    private val stageRepository: KnockoutStageRepository,
    private val tournamentRepository: TournamentRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TournamentComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService,
    env: Environment
) : TwoDeepEntityPage<KnockoutGroupDto, KnockoutStageEntity>(
    "knockout-stage",
    KnockoutGroupDto::class,
    KnockoutStageEntity::class, ::KnockoutStageEntity,
    "Kiesési szakasz", "Kiesési szakaszok",
    "A kiesési szakaszok kezelése.",
    transactionManager,
    object : ManualRepository<KnockoutGroupDto, Int>() {
        override fun findAll(): Iterable<KnockoutGroupDto> {
            val stages = stageRepository.findAllAggregated().associateBy { it.tournamentId }
            val tournaments = tournamentRepository.findAll()
            return tournaments.map {
                KnockoutGroupDto(
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

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "lan",
) {
    override fun fetchSublist(id: Int): Iterable<KnockoutStageEntity> {
        return stageRepository.findAllByTournamentId(id)
    }
}