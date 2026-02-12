package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.controller.admin.OneDeepEntityPage
import hu.bme.sch.cmsch.controller.admin.calculateSearchSettings
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Controller
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.web.bind.annotation.RequestMapping
import tools.jackson.databind.ObjectMapper



@Controller
@RequestMapping("/admin/control/tinder-answer")
@ConditionalOnBean(CommunitiesComponent::class)
class TinderAnswerController(
    val tinderService: TinderService,
    repo: TinderAnswerRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    transactionManager: PlatformTransactionManager,
    env: Environment,
    storageService: StorageService
) : OneDeepEntityPage<TinderAnswerEntity>(
    "tinder-answer",
    TinderAnswerEntity::class, ::TinderAnswerEntity,
    "Tinder válasz", "Tinder válaszok",
    "Tinder válaszok kezelése",

    transactionManager,
    object: ManualRepository<TinderAnswerEntity, Int>(){
        override fun findAll(): MutableIterable<TinderAnswerEntity> {
            return repo.findAllWithUserIdNotNull()
        }
    },
    importService,
    adminMenuService,
    storageService,
    component,
    auditLog,
    objectMapper,
    env,

    showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
    createPermission = StaffPermissions.PERMISSION_CREATE_COMMUNITIES,
    editPermission = StaffPermissions.PERMISSION_EDIT_COMMUNITIES,
    deletePermission = StaffPermissions.PERMISSION_DELETE_COMMUNITIES,

    createEnabled = true,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "inbox",
    adminMenuPriority = 4,

    searchSettings = calculateSearchSettings<TinderAnswerEntity>(false),
) {
}