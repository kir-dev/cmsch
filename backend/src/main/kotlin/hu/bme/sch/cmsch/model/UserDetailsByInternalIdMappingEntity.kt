package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.*
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="userDetailsByInternalId")
data class UserDetailsByInternalIdMappingEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "PéK internal id",
        note = "Ez módosítható eseti hiba kezelésre", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    var internalId: String? = null,

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
        note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 1)
    @property:ImportFormat(ignore = false, columnId = 1)
    var neptun: String? = null,

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 3, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ],
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2, enumSource = RoleType::class)
    var role: RoleType? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 4, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 2)
    var groupName: String? = null,

    @field:JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    var group: GroupEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(columnName = "Gárda", centered = true, order = 3)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_ENUM, enumSource = GuildType::class, defaultValue = "UNKNOWN")
    var guild: GuildType? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_ENUM, enumSource = MajorType::class, defaultValue = "UNKNOWN")
    var major: MajorType? = null,

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 7, label = "Jogosultságok", enabled = true, type = INPUT_TYPE_PERMISSIONS)
    @property:ImportFormat(ignore = false, columnId = 5)
    var permissions: String? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 8, label = "Profilkép", enabled = true)
    @property:ImportFormat(ignore = false, columnId = 6)
    var profilePicture: String? = null,

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 9, label = "Egyedi szöveg a profilhoz", type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_LOB)
    var profileTopMessage: String? = null,
) : ManagedEntity {

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

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}