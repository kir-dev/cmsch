package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
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

        @Column(nullable = false)
        var groupId: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(order = 1, label = "Beadó tankör", enabled = false, ignore = true)
        @property:GenerateOverview(columnName = "Tankör", order = 1)
        var groupName: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(order = 2, label = "Szöveges válasz", enabled = false, ignore = true)
        @property:GenerateOverview(visible = false)
        var textAnswer: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_IMAGE_PREVIEW, order = 3, label = "Beküldött kép", enabled = false, ignore = true)
        @property:GenerateOverview(visible = false)
        var imageUrlAnswer: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 4, label = "Értékelés", note = "Ez a szöveg fog megjelenni a tankörnek")
        @property:GenerateOverview(visible = false)
        var response: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Elfogadva", note = "Ha ez igaz az felülírja az elutasított státuszt")
        @property:GenerateOverview(columnName = "Elfogadva", order = 2, centered = true)
        var approved: Boolean = false,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Elutasítva")
        @property:GenerateOverview(columnName = "Elutasítva", order = 3, centered = true)
        var rejected: Boolean = false,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 7, label = "Adott pont")
        @property:GenerateOverview(columnName = "Pont", order = 4, centered = true)
        var score: Int = 0
)