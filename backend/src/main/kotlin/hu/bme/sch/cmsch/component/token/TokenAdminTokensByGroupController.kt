package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.controller.admin.TwoDeepEntityPage
import hu.bme.sch.cmsch.repository.GroupRepository
import hu.bme.sch.cmsch.repository.ManualRepository
import hu.bme.sch.cmsch.service.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/token-properties-group")
@ConditionalOnBean(TokenComponent::class)
class TokenAdminTokensByGroupController(
    private val repo: TokenPropertyRepository,
    importService: ImportService,
    adminMenuService: AdminMenuService,
    component: TokenComponent,
    auditLog: AuditLogService,
    objectMapper: ObjectMapper,
    private val groupRepository: GroupRepository
) : TwoDeepEntityPage<TokenListByGroupVirtualEntity, TokenVirtualEntity>(
    "token-properties-group",
    TokenListByGroupVirtualEntity::class,
    TokenVirtualEntity::class, ::TokenVirtualEntity,
    "Csoportos tokenek", "Csoportos tokenek",
    "Tokenek csoportonként csoportosítva",

    object : ManualRepository<TokenListByGroupVirtualEntity, Int>() {
        override fun findAll(): Iterable<TokenListByGroupVirtualEntity> {
            return repo.findAll().groupBy { it.ownerGroup?.id ?: 0 }
                .map { it.value }
                .filter { it.isNotEmpty() }
                .map { it ->
                    val groupName = groupRepository.findById(it[0].ownerGroup?.id ?: 0).map { it.name }.orElse("n/a")
                    TokenListByGroupVirtualEntity(
                        it[0].ownerGroup?.id ?: 0,
                        groupName,
                        it.count()
                    )
                }
        }

        override fun delete(entity: TokenListByGroupVirtualEntity) {
            repo.deleteById(entity.id)
        }

        override fun deleteAll() {
            repo.deleteAll()
        }
    },
    object : ManualRepository<TokenVirtualEntity, Int>() {
        override fun delete(entity: TokenVirtualEntity) {
            repo.deleteById(entity.id)
        }

        override fun deleteAll() {
            repo.deleteAll()
        }
    },
    importService,
    adminMenuService,
    component,
    auditLog,
    objectMapper,

    showPermission =   StaffPermissions.PERMISSION_EDIT_TOKENS,
    createPermission = ImplicitPermissions.PERMISSION_NOBODY,
    editPermission =   ImplicitPermissions.PERMISSION_NOBODY,
    deletePermission = StaffPermissions.PERMISSION_EDIT_TOKENS,

    createEnabled = false,
    editEnabled   = false,
    deleteEnabled = true,
    importEnabled = false,
    exportEnabled = false,

    adminMenuIcon = "local_activity",
    adminMenuPriority = 3,
) {

    override fun fetchSublist(id: Int): Iterable<TokenVirtualEntity> {
        return repo.findAllByOwnerGroup_Id(id)
            .map {
                TokenVirtualEntity(
                    it.id,
                    it.token?.title ?: "n/a",
                    it.token?.type ?: "n/a",
                    it.token?.score ?: 0,
                    it.recieved
                )
            }
    }

}

