package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.persistence.*

@Entity
@Table(name="submittedTasks")
@ConditionalOnBean(TaskComponent::class)
data class SubmittedTaskEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    var id: Int = 0,

    @ManyToOne(targetEntity = TaskEntity::class)
    var task: TaskEntity? = null,

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
    @property:GenerateInput(order = 3, label = "Szöveges válasz", enabled = false, ignore = true, type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    var textAnswer: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_IMAGE_PREVIEW, order = 4, label = "Beküldött kép", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var imageUrlAnswer: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_FILE_PREVIEW, order = 5, label = "Beküldött fájl", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var fileUrlAnswer: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 6, label = "Értékelés", note = "Ez a szöveg fog megjelenni a tankörnek")
    @property:GenerateOverview(visible = false)
    var response: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Elfogadva", note = "Ha ez igaz az felülírja az elutasított státuszt")
    @property:GenerateOverview(columnName = "Elfogadva", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var approved: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 8, label = "Elutasítva")
    @property:GenerateOverview(columnName = "Elutasítva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var rejected: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 9, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    var score: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SubmittedTaskEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
