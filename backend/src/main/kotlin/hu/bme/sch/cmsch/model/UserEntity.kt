package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.PermissionGroupService
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.core.env.Environment

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
            return entries.filter { it.value >= type.value }
        }

        fun atMost(type: RoleType): List<RoleType> {
            return entries.filter { it.value <= type.value }
        }

        fun fromValue(value: Int): RoleType? {
            return entries.find { it.value == value }
        }

        @JvmStatic
        fun names(): List<String> = entries.map { it.name }.toList()
    }

    val isAdmin
        get() = value >= ADMIN.value

    val isStaff
        get() = value >= STAFF.value
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
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "PéK internal id",
        note = "Ez módosítható eseti hiba kezelésre", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    override var internalId: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Neptun kód", enabled = true,
        note = "Ez módosítható eseti hiba kezelésre", maxLength = 6)
    @property:GenerateOverview(columnName = "Neptun", order = 2, useForSearch = true)
    @property:ImportFormat
    var neptun: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Cmsch id", enabled = false,
        note = "Automatikusan generálódik a PéK ID-ből")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var cmschId: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Teljes név", enabled = true)
    @property:GenerateOverview(columnName = "Név", order = 1, useForSearch = true)
    @property:ImportFormat
    var fullName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 5, label = "Becenév", enabled = true)
    @property:ImportFormat
    var alias: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "Email cím")
    @property:GenerateOverview(columnName = "Email", order = 4, useForSearch = true)
    @property:ImportFormat
    var email: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 7, label = "Jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    override var role: RoleType = RoleType.GUEST,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 8, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 3, useForSearch = true)
    @property:ImportFormat
    override var groupName: String = "",

    @field:JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    var group: GroupEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 9, label = "Gárda", source = [ "UNKNOWN", "BLACK", "BLUE", "RED", "WHITE", "YELLOW", "PURPLE" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var guild: GuildType = GuildType.UNKNOWN,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 10, label = "Szak", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var major: MajorType = MajorType.UNKNOWN,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 11, label = "Forrás", note = "Honnan jön az adat (authsch, google, keycloak)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var provider: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 12, label = "Profilkép", enabled = true)
    @property:ImportFormat
    var profilePicture: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 13, label = "Jogviszonyok", note = "Melyik kar, milyen szak, aktív-e és gólya-e?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var unitScopes: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 15, label = "Egyedi szöveg a profilhoz", type = InputType.BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var profileTopMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 16, label = "Importált adatok", note = "Volt-e már máshonnan importálva adat")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var detailsImported: Boolean = false,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 17, label = "Konfigurációs beállítások", type = InputType.BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var config: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @ColumnDefault("''")
    @property:GenerateInput(order = 18, label = "Jogosultságok", enabled = true, type = InputType.PERMISSION_GROUPS, maxLength = 20000)
    @property:ImportFormat
    var permissionGroups: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 19, label = "Jogosultságok", enabled = true, type = InputType.PERMISSIONS, maxLength = 20000)
    @property:ImportFormat
    var permissions: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @ColumnDefault("false")
    @property:GenerateInput(type = InputType.SWITCH, order = 20, label = "Service Account", note = "Lehet-e API keyekkel irányitani")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var isServiceAccount: Boolean = false,

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

    @get:Transient
    val permissionGroupsResolved
        get() = PermissionGroupService.getBean().resolvePermissionGroups(permissionGroups)

    val fullNameWithAlias: String
        get() = if (alias != "") "$fullName ($alias)" else fullName

    override var permissionsAsList
        get() = permissions.split(",") + permissionGroupsResolved.split(",")
        set(value) = throw RuntimeException("Value cannot be changed")

    var permissionGroupsAsList
        get() = permissionGroups.split(",")
        set(value) = throw RuntimeException("Value cannot be changed")

    override val userName
        get() = fullName

    override fun hasPermission(permission: String): Boolean {
        return permission.isNotEmpty() && permissionsAsList.contains(permission)
    }
}
