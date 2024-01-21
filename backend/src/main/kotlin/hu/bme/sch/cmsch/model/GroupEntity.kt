package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonIgnore
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

@Entity
@Table(
    name="groups",
    indexes = [Index(name = "idx_group_name", columnList = "name")]
)
data class GroupEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Csoport neve")
    @property:GenerateOverview(columnName = "Csoport", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 2, label = "Típus", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_ENUM, enumSource = MajorType::class)
    var major: MajorType = MajorType.UNKNOWN,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 3, label = "TSZ 1: Név| Facebook url| Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "Kiss Pista | fb.com/pista1234 | +36 30 6969 420")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3)
    var staff1: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 4, label = "TSZ 2: Név| Facebook url| Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4)
    var staff2: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 5, label = "TSZ 3: Név| Facebook url| Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5)
    var staff3: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 6, label = "TSZ 4: Név| Facebook url| Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var staff4: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 7, label = "Csoport borítóképe")
    @property:GenerateOverview(visible = false)
    var coverImageUrl: String = "",

    @field:JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, targetEntity = UserEntity::class, mappedBy = "group")
    @property:GenerateInput(type = INPUT_TYPE_LIST_ENTITIES, order = 12, label = "Csoport tagjai",
            ignore = true, enabled = false, entitySource = "UserEntity")
    @property:GenerateOverview(visible = false)
    var members: List<UserEntity> = listOf(),

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "Játszik a csoport a versenyben?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_BOOLEAN)
    var races: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 14, label = "Kiválasztható", note = "Szabadon válaszható a csoport")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_BOOLEAN)
    var selectable: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 15, label = "Elhagyható", note = "A csoport tagjai megváltoztathatják a csoportjukat")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_BOOLEAN)
    var leaveable: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 16, label = "Felhasználó készítette", note = "A csoportot egy felhasználó hozta létre és nem egy admin")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_BOOLEAN)
    var manuallyCreated: Boolean = false,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 17, label = "Leírás", type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LOB)
    var description: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 18, label = "Egyedi szöveg a profilhoz", type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_LOB)
    var profileTopMessage: String = "",

    @Column(nullable = true)
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 19, label = "Logó url", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12)
    var logo: String? = null,

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Group",
        view = "control/groups",
        showPermission = StaffPermissions.PERMISSION_SHOW_GROUPS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as GroupEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
