package hu.bme.sch.cmsch.component.team

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping


@Repository
interface TeamIntroductionRepository :
    CrudRepository<TeamIntroductionEntity, Int>,
    EntityPageDataSource<TeamIntroductionEntity, Int> {

    @Query("SELECT t FROM TeamIntroductionEntity t WHERE t.group.id = :groupId ORDER BY t.creationDate DESC")
    fun findIntroductionsForGroup(groupId: Int): List<TeamIntroductionEntity>
}

@Controller
@RequestMapping("/admin/control/team-introductions")
@ConditionalOnBean(TeamComponent::class)
class TeamIntroductionReviewController(
    private val teamIntroductionRepository: TeamIntroductionRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TeamComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment
) : OneDeepEntityPage<TeamIntroductionEntity>(
    "team-introductions",
    TeamIntroductionEntity::class, ::TeamIntroductionEntity,
    "Bemutatkozás", "Bemutatkozások",
    "Csapatok bemutatkozásainak ellenőrzése",

    transactionManager,
    teamIntroductionRepository,
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_TEAM_INTRODUCTIONS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission = StaffPermissions.PERMISSION_EDIT_TEAM_INTRODUCTIONS,
    deletePermission = ImplicitPermissions.PERMISSION_NOBODY,

    createEnabled = false,
    editEnabled = true,
    deleteEnabled = false,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "rate_review",
    adminMenuPriority = 2,

    searchSettings = calculateSearchSettings<TeamIntroductionEntity>(false)
)
{
    override fun fetchOverview(user: CmschUser): Iterable<TeamIntroductionEntity> {
        return teamIntroductionRepository.findAll()
            .groupBy { it.group?.id ?: 0 }
            .map { entries -> entries.value.maxBy { it.creationDate } }
    }
}