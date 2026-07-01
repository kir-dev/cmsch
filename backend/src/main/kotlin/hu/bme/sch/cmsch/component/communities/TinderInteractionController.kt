package hu.bme.sch.cmsch.component.communities

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
import tools.jackson.databind.ObjectMapper

@Controller
@RequestMapping("/admin/control/tinder-interactions")
@ConditionalOnBean(CommunitiesComponent::class)
class TinderInteractionController(
    private val communityRepository: CommunityRepository,
    private val tinderService: TinderService,
    tinderInteractionRepository: TinderInteractionRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: CommunitiesComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    env: Environment,
    transactionManager: PlatformTransactionManager,
    storageService: StorageService
) : TwoDeepEntityPage<TinderCommunityVirtualEntity, TinderInteractionEntity>(
    "tinder-interactions",
    TinderCommunityVirtualEntity::class,
    TinderInteractionEntity::class, ::TinderInteractionEntity,
    "Interakció", "Interakciók",
    "Userek interakciói közösségekként csoportosítva",

    transactionManager,
    object: ManualRepository<TinderCommunityVirtualEntity, Int>() {
        override fun findAll(): MutableIterable<TinderCommunityVirtualEntity> {
            return tinderService.getAllInteractions()
                .groupBy { it.communityId }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .mapNotNull { interactions ->
                    communityRepository.findById(interactions[0].communityId)
                        .map { community ->
                            TinderCommunityVirtualEntity(
                                community.id,
                                community.name,
                                interactions.count { it.liked },
                                interactions.count { !it.liked }
                            )
                        }.orElse(null)
                }.toMutableList()
        }
    },
    tinderInteractionRepository,
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

    createEnabled = false,
    editEnabled = true,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "sync_alt",
    adminMenuPriority = 5,
) {

    override fun fetchSublist(id: Int): Iterable<TinderInteractionEntity> {
        return tinderService.getInteractionsByCommunity(id)
    }
}