package hu.bme.sch.g7.model

import hu.bme.sch.g7.admin.*
import javax.persistence.*

enum class AchievementType {
    TEXT,
    IMAGE
}

@Entity
@Table(name="achievements")
data class AchievementEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Feladat neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    var title: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    var category: String = "",

    @Lob
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Leírás")
    @property:GenerateOverview(visible = false)
    var description: String = "",

    @Lob
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 4, label = "Beadandó formátum",
            note = "Ez a beadó mező mellett jelenik meg, külön a leírástól")
    @property:GenerateOverview(visible = false)
    var expectedResultDescription: String = "",

    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Típus", source = [ "TEXT", "IMAGE" ])
    @property:GenerateOverview(visible = false)
    var type: AchievementType = AchievementType.TEXT,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 6, label = "Beadható ekkortól")
    @property:GenerateOverview(columnName = "Innentől", order = 3)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 7, label = "Beadható eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 4)
    var availableTo: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 8, label = "Max pont")
    @property:GenerateOverview(columnName = "Max pont", order = 5, centered = true)
    var maxScore: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 6, centered = true)
    var visible: Boolean = false,

    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 10, label = "Sorrend")
    @property:GenerateOverview(visible = false)
    var order: Long = 0

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }
}