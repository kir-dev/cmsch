package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="freestyleRaceRecords")
@ConditionalOnBean(RaceComponent::class)
data class FreestyleRaceRecordEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Beadás módja", note = "Ez került beadásra, pl: Jäger mérés")
    @property:GenerateOverview(columnName = "Beadás", order = 1)
    @property:ImportFormat
    var description: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat
    var userId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot. Formátum: `id| Teljes Név [a/g] email` ahol az: a = authsch, g = google",
        interpreter = InputInterpreter.SEARCH)
    @property:GenerateOverview(columnName = "Felhasználó", order = 2, centered = true)
    @property:ImportFormat
    var userName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat
    var groupId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 3, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat
    var groupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.FLOAT3, order = 4, label = "Mért idő", defaultValue = "0.0",
        note = "Másodpercben kell megadni, és ponttal (.) van elválasztva, nem vesszővel! 3 tizedes pontig lehet megadni pontosságot.")
    @property:GenerateOverview(columnName = "Idő", order = 4, centered = true)
    @property:ImportFormat
    var time: Float = 0.0f,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Időbélyeg", enabled = false, visible = false)
    @property:GenerateOverview(columnName = "Időbélyeg", order = 5, centered = true, renderer = OverviewType.DATE)
    @property:ImportFormat
    var timestamp: Long = 0,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "FreestyleRaceRecord",
        view = "control/freestyle-race",
        showPermission = StaffPermissions.PERMISSION_SHOW_RACE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as FreestyleRaceRecordEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "FreestyleRaceRecordEntity(id=$id, description='$description', userId=$userId, userName='$userName', groupId=$groupId, groupName='$groupName', time=$time)"
    }

}
