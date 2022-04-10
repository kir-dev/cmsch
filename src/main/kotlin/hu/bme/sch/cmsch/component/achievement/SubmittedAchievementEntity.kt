package hu.bme.sch.cmsch.component.achievement

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.achievement.AchievementEntity
import hu.bme.sch.cmsch.dto.*
import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name="submittedAchievements")
data class SubmittedAchievementEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    var id: Int = 0,

    @ManyToOne(targetEntity = AchievementEntity::class)
    var achievement: AchievementEntity? = null,

    @Column(nullable = true)
    var groupId: Int? = null,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 1, label = "Beadó tankör", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Tankör", order = 1)
    var groupName: String = "",

    @Column(nullable = true)
    var userId: Int? = null,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Beadó felhasználó", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Felhasználó", order = 2)
    var userName: String = "",

    @Column(nullable = false)
    var categoryId: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 3, label = "Szöveges válasz", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var textAnswer: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_IMAGE_PREVIEW, order = 4, label = "Beküldött kép", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var imageUrlAnswer: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 5, label = "Értékelés", note = "Ez a szöveg fog megjelenni a tankörnek")
    @property:GenerateOverview(visible = false)
    var response: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Elfogadva", note = "Ha ez igaz az felülírja az elutasított státuszt")
    @property:GenerateOverview(columnName = "Elfogadva", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var approved: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Elutasítva")
    @property:GenerateOverview(columnName = "Elutasítva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var rejected: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 8, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    var score: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SubmittedAchievementEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
