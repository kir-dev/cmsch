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
    @property:GenerateInput(order = 2, label = "Kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var category: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
    var userId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot")
    @property:GenerateOverview(columnName = "Felhasználó", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3)
    var userName: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    var groupId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5)
    var groupName: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_TIME, order = 9, label = "Mért idő", defaultValue = "0", note = "A mért idő másodperc és ezredmásodperc részét külön kell felvinni")
    @property:GenerateOverview(columnName = "Idő", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 6)
    var time: Float = 0.0f,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 10, label = "Időbélyeg", enabled = false, visible = false)
    @property:GenerateOverview(columnName = "Időbélyeg", order = 6, centered = true, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 7)
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
