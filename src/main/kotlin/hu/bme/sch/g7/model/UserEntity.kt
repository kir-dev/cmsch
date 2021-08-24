package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

enum class RoleType(val value: Int) {
    GUEST(0),      // anyone without login
    BASIC(1),      // has auth.sch but not member of SSSL
    STAFF(100),    // member of the SSSL
    ADMIN(200),    // the organizers of the event
    SUPERUSER(500) // advanced user management (able to grant admin access)
}

enum class GuildType {
    UNKNOWN,
    BLACK,
    BLUE,
    RED,
    WHITE,
    YELLOW
}

enum class MajorType {
    UNKNOWN,
    IT,
    EE,
    BPROF
}

@Entity
@Table(name="users")
data class UserEntity(
        @Id
        @GeneratedValue
        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 1, label = "PéK internal id",
                note = "Ez módosítható eseti hiba kezelésre", enabled = true)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 0)
        var pekId: String = "",

        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
                note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
        @property:GenerateOverview(columnName = "Neptun", order = 2)
        @property:ImportFormat(ignore = false, columnId = 1)
        var neptun: String = "",

        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 3, label = "Gólyahét id", enabled = false,
                note = "Automatikusan generálódik a PéK ID-ből")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 2)
        var g7id: String = "",

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 4, label = "Teljes név", enabled = true)
        @property:GenerateOverview(columnName = "Név", order = 1)
        @property:ImportFormat(ignore = false, columnId = 3)
        var fullName: String = "",

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 19, label = "Becenév", enabled = true)
        var alias: String = "",

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 5, label = "Email cím", note = "Nem kell egyik funkcióhoz sem")
        @property:GenerateOverview(visible = false)
        var email: String = "",

        @JsonView(value = [ Edit::class ])
        @Enumerated(EnumType.STRING)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6, label = "Jogkör",
                source = [ "GUEST", "BASIC", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
                note = "BASIC = gólya, STAFF = senior, ADMIN = minden jog")
        @property:GenerateOverview(visible = false)
        var role: RoleType = RoleType.GUEST,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 10, label = "JOG: Bármiylen termék eladása", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_BOOLEAN)
        var grantSellProduct: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "JOG: Kaja eladása", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_BOOLEAN)
        var grantSellFood: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 12, label = "JOG: Merch eladása", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_BOOLEAN)
        var grantSellMerch: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "JOG: PR", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_BOOLEAN)
        var grantMedia: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 14, label = "JOG: Bucketlist értékelés", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_BOOLEAN)
        var grantRateAchievement: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 15, label = "JOG: Bucketlist létrehozása", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_BOOLEAN)
        var grantCreateAchievement: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 16, label = "JOG: Infópult", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_BOOLEAN)
        var grantListUsers: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 17, label = "JOG: Gárdatanköris", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_BOOLEAN)
        var grantGroupManager: Boolean = false,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 18, label = "JOG: Tankörös pénzek intézése", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_BOOLEAN)
        var grantGroupDebtsMananger: Boolean = false,

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 7, label = "Tankör", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
        @property:GenerateOverview(columnName = "Tankör", centered = true, order = 3)
        var groupName: String = "",

        @JsonIgnore
        @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
        var group: GroupEntity? = null,

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Enumerated(EnumType.STRING)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 8, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
        @property:GenerateOverview(columnName = "Gárda", centered = true, order = 4)
        @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_ENUM, enumSource = GuildType::class, defaultValue = "UNKNOWN")
        var guild: GuildType = GuildType.UNKNOWN,

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Enumerated(EnumType.STRING)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 9, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_ENUM, enumSource = MajorType::class, defaultValue = "UNKNOWN")
        var major: MajorType = MajorType.UNKNOWN

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $fullName neptun:$neptun pek:$pekId"
    }

    fun isAdmin(): Boolean {
        return role == RoleType.ADMIN || role == RoleType.SUPERUSER
    }
}