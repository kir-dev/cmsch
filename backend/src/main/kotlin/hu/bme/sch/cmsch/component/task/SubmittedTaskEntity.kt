package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.admin.dashboard.SubmissionHistory
import hu.bme.sch.cmsch.admin.dashboard.historyReader
import hu.bme.sch.cmsch.admin.dashboard.historyWriter
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
@Table(name="submittedTasks",
    indexes = [
        Index(name = "index_task", columnList = "task_id"),
        Index(name = "index_groupId", columnList = "groupId"),
        Index(name = "index_userId", columnList = "userId"),
        Index(name = "index_categoryId", columnList = "categoryId"),
        Index(name = "index_task_group", columnList = "task_id,groupId"),
        Index(name = "index_task_user", columnList = "task_id,userId")
    ]
)
@ConditionalOnBean(TaskComponent::class)
data class SubmittedTaskEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @ManyToOne(targetEntity = TaskEntity::class)
    var task: TaskEntity? = null,

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 0)
    var groupId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 1, label = "Beadó csoport", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var groupName: String = "",

    @Column(nullable = true)
    var userId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Beadó felhasználó", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Felhasználó", order = 2)
    var userName: String = "",

    @Column(nullable = false)
    var categoryId: Int = 0,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 4, label = "Szöveges válasz (teljes)", enabled = false, ignore = true, type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    var textAnswerLob: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_IMAGE_PREVIEW, order = 5, label = "Beküldött kép", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var imageUrlAnswer: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_FILE_PREVIEW, order = 6, label = "Beküldött fájl", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    var fileUrlAnswer: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 7, label = "Értékelés", note = "Ez a szöveg fog megjelenni a csoportoknak/beadóknak")
    @property:GenerateOverview(visible = false)
    var response: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 8, label = "Elfogadva", note = "Ha ez igaz az felülírja az elutasított státuszt")
    @property:GenerateOverview(columnName = "Elfogadva", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var approved: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Elutasítva")
    @property:GenerateOverview(columnName = "Elutasítva", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var rejected: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 10, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    var score: Int = 0,

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT default ''")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 11, label = "Beadás történet", type = INPUT_TYPE_TASK_SUBMISSION_HISTORY)
    @property:GenerateOverview(visible = false)
    var submissionHistory: String = "",
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "SubmittedTask",
        view = "control/rate-tasks",
        showPermission = StaffPermissions.PERMISSION_RATE_TASKS
    )

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

    fun addSubmissionHistory(
        date: Long,
        submitterName: String,
        adminResponse: Boolean,
        content: String,
        contentUrl: String,
        status: String,
        type: String
    ) : SubmittedTaskEntity {
        val history = if (submissionHistory.isBlank()) {
            mutableListOf<SubmissionHistory>()
        } else {
            historyReader.readValue(submissionHistory)
        }

        history.add(SubmissionHistory(date, submitterName, adminResponse, content, contentUrl, status, type))

        submissionHistory = historyWriter.writeValueAsString(history)
        return this
    }
}
