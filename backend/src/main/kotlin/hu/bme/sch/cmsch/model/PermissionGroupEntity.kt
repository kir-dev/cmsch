package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.core.env.Environment

@Entity
@Table(name = "permissionGroups")
data class PermissionGroupEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`key`")
    @property:GenerateInput(order = 1, label = "Kulcs", enabled = true, note = "Ideális egyedinek lennie")
    @property:GenerateOverview(columnName = "Kulcs", order = 1, useForSearch = true)
    @property:ImportFormat
    var key: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Megjelenő név", enabled = true, note = "Ez csak a felhasználóknak lesz így kijelezve")
    @property:GenerateOverview(columnName = "Név", order = 1, useForSearch = true)
    @property:ImportFormat
    var displayName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 3, label = "Jogosultságok", enabled = true, type = InputType.PERMISSIONS, maxLength = 20000)
    @property:ImportFormat
    var permissions: String = "",
) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "PermissionGroup",
        view = "control/permission-groups",
        showPermission = StaffPermissions.PERMISSION_SHOW_PERMISSION_GROUPS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as PermissionGroupEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    override fun duplicate(): PermissionGroupEntity {
        return this.copy()
    }

}
