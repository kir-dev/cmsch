import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.*
import javax.persistence.*

@Entity
@Table(name="userDetailsByInternalId")
data class UserDetailsByInternalIdMappingEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "PéK internal id",
        note = "Ez módosítható eseti hiba kezelésre", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    var internalId: String? = null,

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
        note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var neptun: String? = null,

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 7, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, enumSource = RoleType::class, defaultValue = "GUEST")
    var role: RoleType? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 8, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 3)
    var groupName: String? = null,

    @JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    var group: GroupEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 9, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(columnName = "Gárda", centered = true, order = 5)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_ENUM, enumSource = GuildType::class, defaultValue = "UNKNOWN")
    var guild: GuildType? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 10, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_ENUM, enumSource = MajorType::class, defaultValue = "UNKNOWN")
    var major: MajorType? = null,

    @Lob
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 16, label = "Jogosultságok", enabled = true, type = INPUT_TYPE_PERMISSIONS)
    @property:ImportFormat(ignore = false, columnId = 9)
    var permissions: String? = null,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 12, label = "Profilkép", enabled = true)
    @property:ImportFormat(ignore = false, columnId = 11)
    var profilePicture: String? = null,

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB default ''")
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 15, label = "Egyedi szöveg a profilhoz", type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_LOB)
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
}