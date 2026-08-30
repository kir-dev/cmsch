package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.core.env.Environment

@Entity
@Table(name = "roleToUser", indexes = [
    Index(name = "neptun_unique", columnList = "neptun", unique = true),
    Index(name = "email_unique", columnList = "email", unique = true),
])
data class RoleToUserMappingEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = true, name = "neptun")
    @property:GenerateInput(maxLength = 6, order = 1, label = "Neptun kód")
    @property:GenerateOverview(columnName = "Neptun kód", order = 1)
    @property:ImportFormat
    var neptun: String? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = true, name = "email")
    @property:GenerateInput(order = 2, label = "E-mail cím")
    @property:GenerateOverview(columnName = "E-mail cím", order = 2)
    @property:ImportFormat
    var email: String? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 3)
    @property:ImportFormat
    var fullName: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 4, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(columnName = "Jogkör", order = 4, centered = true)
    @property:ImportFormat
    var role: RoleType = RoleType.BASIC

): ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "RoleToUser",
        view = "control/role-to-user",
        showPermission = StaffPermissions.PERMISSION_SHOW_ROLE_MAPPINGS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RoleToUserMappingEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    override fun duplicate(): RoleToUserMappingEntity {
        return this.copy()
    }

}
