package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.persistence.*

enum class AchievementType {
    TEXT,
    IMAGE,
    BOTH
}

@Entity
@Table(name="achievements")
@ConditionalOnBean(AchievementComponent::class)
data class AchievementEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Feladat neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var categoryId: Int = 0,

    @Lob
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 3, label = "Leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var description: String = "",

    @Lob
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 4, label = "Beadandó formátum",
            note = "Ez a beadó mező mellett jelenik meg, külön a leírástól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3)
    var expectedResultDescription: String = "",

    @Enumerated(EnumType.STRING)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Típus", source = [ "TEXT", "IMAGE", "BOTH" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_ENUM, enumSource = AchievementType::class)
    var type: AchievementType = AchievementType.TEXT,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Beadható ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 7, label = "Beadható eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 4, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LONG)
    var availableTo: Long = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 8, label = "Max pont")
    @property:GenerateOverview(columnName = "Max pont", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_INT)
    var maxScore: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 10, label = "Kiemelt",
            note = "Olyan szöveggel jelenik meg, hogy hamarosan lejár")
    @property:GenerateOverview(columnName = "Kiemelt", order = 7, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_BOOLEAN)
    var highlighted: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 11, label = "Sorrend")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LONG)
    var order: Long = 0

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AchievementEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
}
