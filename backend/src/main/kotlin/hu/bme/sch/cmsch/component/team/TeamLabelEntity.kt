package hu.bme.sch.cmsch.component.team

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import jakarta.persistence.*
import org.springframework.core.env.Environment

@Entity
@Table(
    name = "teamLabel",
    indexes = [Index(columnList = "name", unique = true)]
)
data class TeamLabelEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 1, label = "Csoport", entitySource = "GroupEntity", minimumRole = RoleType.STAFF)
    @property:GenerateOverview(columnName = "Csoport", centered = true, order = 3, useForSearch = true)
    @property:ImportFormat
    var groupName: String = "",

    @field:JsonIgnore
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.EAGER)
    var group: GroupEntity? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(order = 2, label = "A címke ami megjelenik")
    @property:GenerateOverview(columnName = "Címke", order = 2)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = true, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(
        order = 3,
        label = "Szín",
        note = "A címke színe hex kódban megadva. Formátum #FFGGBB. Ha üresen hagyod, akkor az oldal színét fogja használni.",
    )
    @property:ImportFormat
    var color: String = "",

    @Column(nullable = true, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @property:GenerateInput(
        order = 4,
        label = "Leírás",
        note = "Részletes leírás ami megjelenik, mikor az egeret a címkére görgetjük.",
    )
    @property:ImportFormat
    var desc: String = "",

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(
        type = InputType.SWITCH,
        order = 5,
        label = "Listába megjelenik",
        note = "Ha nincs bekapcsolva, akkor csak a csoport részletes nézetén fog megjelenni"
    )
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var showList: Boolean = false,

) : ManagedEntity {

    val groupId
        get() = group?.id

    override fun getEntityConfig(env: Environment) = null

}