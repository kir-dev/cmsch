package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

enum class RoleType {
    GUEST,    // anyone without login
    BASIC,    // has auth.sch but not member of SSSL
    STAFF,    // member of the SSSL
    ADMIN,    // the organizers of the event
    SUPERUSER // advanced user management (able to grant admin access)
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
    @property:GenerateInput(order = 1, label = "Pék internal id", enabled = false)
    @property:GenerateOverview(visible = false)
    var pekId: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true, note = "Ez módosítható eseti hiba kezelésre")
    @property:GenerateOverview(columnName = "Neptun", order = 2)
    var neptun: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Teljes név", enabled = true)
    @property:GenerateOverview(columnName = "Név", order = 1)
    var fullName: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Email cím")
    @property:GenerateOverview(visible = false)
    var email: String = "",

    @JsonView(value = [ Edit::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Típus", source = [ "GUEST", "BASIC", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    var role: RoleType = RoleType.GUEST,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "JOG: Merch eladása")
    @property:GenerateOverview(visible = false)
    var grantSellProduct: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 10, label = "JOG: Kaja eladása")
    @property:GenerateOverview(visible = false)
    var grantSellFood: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "JOG: PR")
    @property:GenerateOverview(visible = false)
    var grantMedia: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 12, label = "JOG: Bucketlist értékelés")
    @property:GenerateOverview(visible = false)
    var grantRateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "JOG: Bucketlist létrehozása")
    @property:GenerateOverview(visible = false)
    var grantCreateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 14, label = "JOG: Felhasználó kezelés")
    @property:GenerateOverview(visible = false)
    var grantManageUsers: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 15, label = "JOG: Infópult")
    @property:GenerateOverview(visible = false)
    var grantListUsers: Boolean = false,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    // FIXME: set tankör | ez látszódjon is | order 6
    var groupName: String = "",

    @JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.LAZY)
    // FIXME: set tankör
    var group: GroupEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 7, label = "Típus", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(columnName = "Gárda", centered = true, order = 3)
    var guild: GuildType = GuildType.UNKNOWN,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 8, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    var major: MajorType = MajorType.UNKNOWN

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $fullName neptun:$neptun pek:$pekId"
    }
}