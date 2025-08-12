package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
@Table(name="userDetailsByInternalId")
data class UserDetailsByInternalIdMappingEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "PéK internal id",
        note = "Ez módosítható eseti hiba kezelésre", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var internalId: String? = null,

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
        note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 1)
    @property:ImportFormat
    var neptun: String? = null,

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 3, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ],
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var role: RoleType? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 4, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 2)
    var groupName: String? = null,

    @field:JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    var group: GroupEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 5, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(columnName = "Gárda", centered = true, order = 3)
    @property:ImportFormat
    var guild: GuildType? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 6, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var major: MajorType? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 7, label = "Jogosultságok", enabled = true, type = InputType.PERMISSIONS)
    @property:ImportFormat
    var permissions: String? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 8, label = "Profilkép", enabled = true)
    @property:ImportFormat
    var profilePicture: String? = null,

    @Column(columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 9, label = "Egyedi szöveg a profilhoz", type = InputType.BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var profileTopMessage: String? = null,
) : ManagedEntity, Duplicatable {

    fun allDetailsImported(): Boolean {
        return  neptun != null &&
                internalId != null &&
                role != null &&
                groupName != null &&
                guild != null &&
                major != null &&
                permissions != null &&
                profilePicture != null &&
                profileTopMessage != null
    }

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "UserDetailsByInternalId",
        view = "control/user-details-by-internal-id",
        showPermission = StaffPermissions.PERMISSION_SHOW_GUILD_MAPPINGS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserDetailsByInternalIdMappingEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun duplicate(): UserDetailsByInternalIdMappingEntity {
        return this.copy()
    }

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
