package hu.bme.sch.cmsch.component.key

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="accessKeys")
@ConditionalOnBean(AccessKeyComponent::class)
data class AccessKeyEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Kulcs")
    @property:GenerateOverview(columnName = "Kulcs", order = 1)
    @property:ImportFormat
    var accessKey: String = "",

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @property:GenerateInput(maxLength = 128, order = 2, label = "Cimke",
        note = "Például a név aminek majd szerepelnie kellene")
    @property:GenerateOverview(columnName = "Cimke", order = 3)
    @property:ImportFormat
    var tag: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 10, label = "Felhasználó ID-je",
        type = InputType.NUMBER, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var usedByUserId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 11, label = "Felhasználó neve",
        note = "Ez csak logolás miatt van ideírva")
    @property:GenerateOverview(columnName = "Felhasználó", order = 2)
    @property:ImportFormat
    var usedByUserName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 4, label = "Csoport átállítása",
        note = "Ha be van kapcsolva, akkor az alább látható csoportot fogja beállítani a kitöltőnek")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var setGroup: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 5, label = "Csoport neve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var groupName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 6, label = "Szerep átállítása",
        note = "Ha be van kapcsolva, akkor az alább látható szerepet fogja beállítani a kitöltőnek")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var setRole: Boolean = false,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 7,
        label = "Szerepkör",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ],
        defaultValue = "ATTENDEE")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var roleType: RoleType = RoleType.ATTENDEE,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 12, label = "Mikor használta fel", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var usedAt: Long = 0,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "AccessKey",
        view = "control/access-key",
        showPermission = StaffPermissions.PERMISSION_SHOW_ACCESS_KEYS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AccessKeyEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "AccessKeyEntity(id=$id, accessKey='$accessKey', usedByUserName='$usedByUserName', usedAt=$usedAt)"
    }

}
