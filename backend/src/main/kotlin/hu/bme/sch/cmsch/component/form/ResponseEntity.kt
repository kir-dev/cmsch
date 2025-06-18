package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.hibernate.annotations.ColumnDefault
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

private val mapper = ObjectMapper()
private val mapType = object : TypeReference<Map<String, String>>() {}

@Entity
@Table(name="formResponses", indexes = [Index(columnList = "entryToken")])
@ConditionalOnBean(FormComponent::class)
data class ResponseEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var submitterUserId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Beküldő neve", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Beküldő", order = 1)
    @property:ImportFormat
    var submitterUserName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var submitterGroupId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Beküldő csoport", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "", order = 2)
    @property:ImportFormat
    var submitterGroupName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var formId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Beküldve ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var creationDate: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Utoljára módosítva", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var lastUpdatedDate: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 7, label = "Fizetve")
    @property:GenerateOverview(columnName = "Fizetve", order = 4, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var accepted: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Fizetve ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var acceptedAt: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 7, label = "Elutasítva")
    @property:GenerateOverview(columnName = "Elutasítva", order = 5, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var rejected: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 255, order = 8, label = "Elutasítás indoka", note = "Csak akkor kell ha elutasított")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var rejectionMessage: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 255, order = 9, label = "Email",
        note = "A felhasználó email címe, ez a BME Jegy integrációhoz kell")
    @property:GenerateOverview(columnName = "Beküldő emailje", order = 2)
    @property:ImportFormat
    var email: String = "",

    @Column(nullable = false)
    @ColumnDefault("''")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 255, order = 9, label = "Beléptető token",
        note = "Anonim kitöltéskor generált beléptető token")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var entryToken: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 255, order = 10, label = "Kitöltés",
        note = "A kitöltés JSON formátumban", type = InputType.BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var submission: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 11, label = "Adatok elfogadva")
    @property:GenerateOverview(columnName = "Adatok", order = 6, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var detailsValidated: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.DATE, order = 12, label = "Adatok elfogadva ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var detailsValidatedAt: Long = 0,

    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 13, label = "Beadás történet", type = InputType.TASK_SUBMISSION_HISTORY)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var rejectionHistory: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 14, label = "Sorszám", type = InputType.NUMBER, note = "Exportokhoz van használva")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var line: Int = 0,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Response",
        view = "control/signup-responses",
        showPermission = StaffPermissions.PERMISSION_SHOW_FORM_RESULTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ResponseEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "ResponseEntity(id=$id, submitterUserName='$submitterUserName', submitterGroupName='$submitterGroupName', formId=$formId)"
    }

    fun readSubmission(): Map<String, String> = mapper.readValue(submission, mapType)

    fun addHistory(
        date: Long,
        submitterName: String,
        adminResponse: Boolean,
        content: String,
        contentUrl: String,
        status: String,
        type: String
    ) : ResponseEntity {
        val history = if (rejectionHistory.isBlank()) {
            mutableListOf<SubmissionHistory>()
        } else {
            historyReader.readValue(rejectionHistory)
        }

        history.add(SubmissionHistory(date, submitterName, adminResponse, content, contentUrl, status, type))

        rejectionHistory = historyWriter.writeValueAsString(history)
        return this
    }

    fun getImmutableHistory(): List<SubmissionHistory> {
        return if (rejectionHistory.isBlank()) {
            mutableListOf()
        } else {
            historyReader.readValue(rejectionHistory)
        }
    }

    fun getUserResubmissionCount(): Int {
        return getImmutableHistory().count { !it.adminResponse }
    }

}
