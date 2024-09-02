package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.core.env.Environment
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "groupToUser", indexes = [
    Index(name = "neptun_unique", columnList = "neptun", unique = true),
    Index(name = "email_unique", columnList = "email", unique = true),
])
data class GroupToUserMappingEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
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
    @property:GenerateInput(order = 2, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 3)
    @property:ImportFormat
    var fullName: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 3, label = "Csoport", entitySource = "GroupEntity")
    @property:GenerateOverview(columnName = "Csoport", order = 4, centered = true)
    @property:ImportFormat
    var groupName: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 4, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(columnName = "Szak", order = 5, centered = true)
    @property:ImportFormat
    var major: MajorType = MajorType.UNKNOWN

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "GroupToUser",
        view = "control/group-to-user",
        showPermission = StaffPermissions.PERMISSION_SHOW_GROUP_MAPPINGS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GroupToUserMappingEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
