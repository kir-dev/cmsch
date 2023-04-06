package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/riddles-by-users")
@ConditionalOnBean(RiddleComponent::class)
class TokenAdminTokensByGroupController(
    private val riddleMappingRepository: RiddleMappingRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : TwoDeepEntityPage<RiddleStatsVirtualEntity, RiddleMappingVirtualEntity>(
    "riddles-by-users",
    RiddleStatsVirtualEntity::class,
    RiddleMappingVirtualEntity::class, ::RiddleMappingVirtualEntity,
    "Riddle beadás felhasználónként", "Riddle felasználónként",
    "Beadott riddleök felhasználónként csoportosítva",

    object : ManualRepository<RiddleStatsVirtualEntity, Int>() {
        override fun findAll(): Iterable<RiddleStatsVirtualEntity> {
            return riddleMappingRepository.findAll().groupBy { it.ownerUser?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    RiddleStatsVirtualEntity(
                        submissions[0].ownerUser?.id ?: 0,
                        submissions[0].ownerUser?.fullName ?: "n/a",
                        submissions.count { it.completed },
                        submissions.count { it.hintUsed }
                    )
                }
        }

        override fun delete(entity: RiddleStatsVirtualEntity) {
            riddleMappingRepository.deleteById(entity.id)
        }

        override fun deleteAll() {
            riddleMappingRepository.deleteAll()
        }
    },
    object : ManualRepository<RiddleMappingVirtualEntity, Int>() {
        override fun delete(entity: RiddleMappingVirtualEntity) {
            riddleMappingRepository.deleteById(entity.id)
        }

        override fun deleteAll() {
            riddleMappingRepository.deleteAll()
        }
    },
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   ImplicitPermissions.PERMISSION_NOBODY,
    deletePermission = StaffPermissions.PERMISSION_SHOW_DELETE_RIDDLE_SUBMISSIONS,

    createEnabled = false,
    editEnabled   = false,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "checklist_rtl",
    adminMenuPriority = 3,
) {

    override fun fetchSublist(id: Int): Iterable<RiddleMappingVirtualEntity> {
        return riddleMappingRepository.findAllByOwnerUser_Id(id)
            .map { submission ->
                RiddleMappingVirtualEntity(
                    submission.id,
                    submission.riddle?.categoryId ?: 0,
                    submission.riddle?.title ?: "n/a",
                    submission.hintUsed,
                    submission.completed,
                    submission.attemptCount,
                    submission.completedAt
                )
            }
    }

}

