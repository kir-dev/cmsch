package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.core.env.Environment
import java.lang.RuntimeException
import jakarta.persistence.*

enum class RoleType(val value: Int, val displayName: String, val description: String) {
    GUEST(0, "Vendég", "Nem bejelentkezett felhasználó"),
    BASIC(1, "Felhasználó", "Bejelentkezett felhasználó"),
    ATTENDEE(2, "Résztvevő", "Résztvevő, de sima felhasználó"),
    PRIVILEGED(3, "Kiemelt", "Résztvevő, de megemelt jogkörökkel"),
    STAFF(100, "Rendező", "Rendező, akinek az admin felületre be kellhet lépnie"),
    ADMIN(200, "Adminisztrátor", "Adminisztrátor, aki mindenhez hozzáfér"),
    SUPERUSER(500, "Fejlesztő", "Adminisztrátor, de a fejlesztői menük is látszanak"),
    NOBODY(Int.MAX_VALUE, "", "")
    ;

    companion object {
        fun atLeast(type: RoleType): List<RoleType> {
            return values().filter { it.value >= type.value }
        }

        fun atMost(type: RoleType): List<RoleType> {
            return values().filter { it.value <= type.value }
        }

        @JvmStatic
        fun names(): List<String> = values().map { it.name }.toList()
    }

    val isAdmin
        get() = value >= ADMIN.value
}

enum class GuildType(val displayName: String) {
    UNKNOWN("n/a"),
    BLACK("fekete"),
    BLUE("kék"),
    RED("piros"),
    WHITE("fehér"),
    YELLOW("sárga"),
    PURPLE("lila"),
}

enum class MajorType {
    UNKNOWN,
    IT,
    EE,
    BPROF
}

@Entity
@Table(name = "users", indexes = [
    Index(name = "idx_userentity_internalid_unq", columnList = "internalId", unique = true),
    Index(name = "idx_userentity_cmschid", columnList = "cmschId"),
    Index(name = "idx_userentity_neptun", columnList = "neptun"),
    Index(name = "idx_userentity_groupname", columnList = "groupName"),
    Index(name = "idx_userentity_email", columnList = "email"),
    Index(name = "idx_userentity_group", columnList = "group_id")
])
data class UserEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "PéK internal id",
        note = "Ez módosítható eseti hiba kezelésre", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    override var internalId: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
        note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 2, useForSearch = true)
    @property:ImportFormat(ignore = false, columnId = 1)
    var neptun: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Cmsch id", enabled = false,
        note = "Automatikusan generálódik a PéK ID-ből")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var cmschId: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Teljes név", enabled = true)
    @property:GenerateOverview(columnName = "Név", order = 1, useForSearch = true)
    @property:ImportFormat(ignore = false, columnId = 3)
    var fullName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 5, label = "Becenév", enabled = true)
    @property:ImportFormat(ignore = false, columnId = 4)
    var alias: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "Email cím")
    @property:GenerateOverview(columnName = "Email", order = 4, useForSearch = true)
    @property:ImportFormat(ignore = false, columnId = 5)
    var email: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 7, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, enumSource = RoleType::class, defaultValue = "GUEST")
    override var role: RoleType = RoleType.GUEST,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 8, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 3, useForSearch = true)
    override var groupName: String = "",

    @field:JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    var group: GroupEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 9, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_ENUM, enumSource = GuildType::class, defaultValue = "UNKNOWN")
    var guild: GuildType = GuildType.UNKNOWN,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 10, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_ENUM, enumSource = MajorType::class, defaultValue = "UNKNOWN")
    var major: MajorType = MajorType.UNKNOWN,

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 16, label = "Jogosultságok", enabled = true, type = INPUT_TYPE_PERMISSIONS, maxLength = 20000)
    @property:ImportFormat(ignore = false, columnId = 9)
    var permissions: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 11, label = "Forrás", note = "Honnan jön az adat (authsch, google, keycloak)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10)
    var provider: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 12, label = "Profilkép", enabled = true)
    @property:ImportFormat(ignore = false, columnId = 11)
    var profilePicture: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 13, label = "Jogviszonyok", note = "Melyik kar, milyen szak, aktív-e és gólya-e?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12)
    var unitScopes: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 15, label = "Egyedi szöveg a profilhoz", type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_LOB)
    var profileTopMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 16, label = "Importált adatok", note = "Volt-e már máshonnan importálva adat")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_BOOLEAN)
    var detailsImported: Boolean = false,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 16, label = "Konfigurációs beállítások", type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 15, type = IMPORT_LOB)
    var config: String = "",

): ManagedEntity, CmschUser {

    override val groupId
        get() = group?.id

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "User",
        view = "control/users",
        showPermission = StaffPermissions.PERMISSION_SHOW_USERS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as UserEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    val fullNameWithAlias: String
        get() = if (alias != "") "${fullName} ($alias)" else fullName

    override var permissionsAsList
        get() = permissions.split(",")
        set(value) = throw RuntimeException("Value cannot be changed")

    override val userName
        get() = fullName

    override fun hasPermission(permission: String): Boolean {
        return permissionsAsList.contains(permission)
    }
}
