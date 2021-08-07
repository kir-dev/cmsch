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
    var pekId: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
            note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 2)
    var neptun: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Gólyahét id", enabled = false,
            note = "Automatikusan generálódik a PéK ID-ből")
    @property:GenerateOverview(visible = false)
    var g7id: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Teljes név", enabled = true)
    @property:GenerateOverview(columnName = "Név", order = 1)
    var fullName: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 5, label = "Email cím")
    @property:GenerateOverview(visible = false)
    var email: String = "",

    @JsonView(value = [ Edit::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6, label = "Típus",
            source = [ "GUEST", "BASIC", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var role: RoleType = RoleType.GUEST,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 10, label = "JOG: Merch eladása", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantSellProduct: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "JOG: Kaja eladása", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantSellFood: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 12, label = "JOG: PR", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantMedia: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "JOG: Bucketlist értékelés", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantRateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 14, label = "JOG: Bucketlist létrehozása", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantCreateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 15, label = "JOG: Infópult", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantListUsers: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 16, label = "JOG: Gárdatanköris", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(visible = false)
    var grantGroupManager: Boolean = false,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 7, label = "Tankör", entitySource = "GroupEntity", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(columnName = "Tankör", centered = true, order = 3)
    var groupName: String = "",

    @JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    var group: GroupEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 8, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(columnName = "Gárda", centered = true, order = 4)
    var guild: GuildType = GuildType.UNKNOWN,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 9, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    var major: MajorType = MajorType.UNKNOWN

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $fullName neptun:$neptun pek:$pekId"
    }

    fun isAdmin(): Boolean {
        return role == RoleType.ADMIN /*|| role == RoleType.SUPERUSER*/
    }
}