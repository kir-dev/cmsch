package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.persistence.*

@Entity
@Table(name="raceRecords")
@ConditionalOnBean(RaceComponent::class)
data class RaceRecordEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 2, label = "Kategória", type = INPUT_TYPE_ENTITY_SELECT,
        entitySource = "RaceCategoryEntity", note = "Az üres az alapértelmezett kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var category: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var userId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot. Formátum: `id| Teljes Név [a/g] email` ahol az: a = authsch, g = google",
        interpreter = INTERPRETER_SEARCH)
    @property:GenerateOverview(columnName = "Felhasználó", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 2)
    var userName: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    var groupId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 3, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4)
    var groupName: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_FLOAT, order = 4, label = "Mért idő", defaultValue = "0.0",
        note = "Másodpercben kell megadni, és ponttal (.) van elválasztva, nem vesszővel! 3 tizedes pontig lehet megadni pontosságot.")
    @property:GenerateOverview(columnName = "Idő", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_FLOAT)
    var time: Float = 0.0f,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 5, label = "Időbélyeg", enabled = false, visible = false)
    @property:GenerateOverview(columnName = "Időbélyeg", order = 5, centered = true, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LONG)
    var timestamp: Long = 0,

) : ManagedEntity {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RaceRecordEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "RaceRecordEntity(id=$id, category='$category', userId=$userId, userName='$userName', groupId=$groupId, groupName='$groupName', time=$time)"
    }

}
