package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.controller.admin.ButtonAction
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import hu.bme.sch.cmsch.util.getUser
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/admin/control/riddles-by-groups")
@ConditionalOnBean(RiddleComponent::class)
class RiddlesByGroupsController(
    private val riddleMappingRepository: RiddleMappingRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: RiddleComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper
) : TwoDeepEntityPage<RiddleStatsVirtualEntity, RiddleMappingVirtualEntity>(
    "riddles-by-groups",
    RiddleStatsVirtualEntity::class,
    RiddleMappingVirtualEntity::class, ::RiddleMappingVirtualEntity,
    "Riddle beadás csoportonként", "Riddle csoportonként",
    "Beadott riddleök csoportonként csoportosítva",

    object : ManualRepository<RiddleStatsVirtualEntity, Int>() {
        override fun findAll(): Iterable<RiddleStatsVirtualEntity> {
            return riddleMappingRepository.findAll().groupBy { it.ownerGroup?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { submissions ->
                    RiddleStatsVirtualEntity(
                        submissions[0].ownerGroup?.id ?: 0,
                        submissions[0].ownerGroup?.name ?: "n/a",
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
    adminMenuPriority = 4,

    buttonActions = mutableListOf(
        ButtonAction(
            name = "Riddle statisztika export",
            "filtered-export/csv",
            StaffPermissions.PERMISSION_EDIT_GROUPS,
            300,
            icon = "export_notes",
            primary = false,
            newPage = true
        )
    )
) {

    override fun fetchSublist(id: Int): Iterable<RiddleMappingVirtualEntity> {
        return riddleMappingRepository.findAllByOwnerGroup_Id(id)
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

    data class RiddleByGroupFilteredView(
        @property:ImportFormat(ignore = false, columnId = 0, type = IMPORT_INT)
        var riddleId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 1)
        var riddleName: String = "",

        @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
        var groupId: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 3)
        var groupName: String = "",

        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
        var score: Int = 0,

        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_BOOLEAN)
        var hint: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_BOOLEAN)
        var completed: Boolean = false,

        @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_INT)
        var attemptCount: Int = 0,
    )

    private val filterDescriptor = OverviewBuilder(RiddleByGroupFilteredView::class)

    @ResponseBody
    @GetMapping("/filtered-export/csv", produces = [ MediaType.APPLICATION_OCTET_STREAM_VALUE ])
    fun filteredExport(auth: Authentication, response: HttpServletResponse): ByteArray {
        val user = auth.getUser()
        if (!showPermission.validate(user)) {
            throw IllegalStateException("Insufficient permissions")
        }
        response.setHeader("Content-Disposition", "attachment; filename=\"$view-filtered-export.csv\"")
        return filterDescriptor.exportToCsv(riddleMappingRepository.findAll()
            .filter { it.completed }
            .map {
                RiddleByGroupFilteredView(
                    it.riddle?.id ?: 0,
                    it.riddle?.title ?: "-",
                    it.ownerGroup?.id ?: 0,
                    it.ownerGroup?.name ?: "",
                    it.riddle?.score ?: 0,
                    it.hintUsed,
                    it.completed,
                    it.attemptCount
                )
            }).toByteArray()
    }

}
