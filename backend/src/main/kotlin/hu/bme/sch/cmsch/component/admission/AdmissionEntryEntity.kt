package hu.bme.sch.cmsch.component.admission

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.form.FormEntity
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
@Table(name="admissionEntry")
@ConditionalOnBean(AdmissionComponent::class)
data class AdmissionEntryEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Felhasználó")
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    @property:ImportFormat(ignore = false)
    var userName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 2, label = "Felhasználó ID-je", min = 0, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var userId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Időbélyeg", min = 0, defaultValue = "0")
    @property:GenerateOverview(columnName = "Frissült", order = 3, renderer = OVERVIEW_TYPE_DATE, centered = true)
    @property:ImportFormat(ignore = false)
    var timestamp: Long = 0L,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 4, label = "Űrlap ID-je", min = 0, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var formId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 5, label = "Beadás ID-je", min = 0, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var responseId: Int = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6,
        label = "Belépési jogosultság",
        source = [ "BANNED", "CANNOT_ATTEND", "USER", "ORGANIZER", "VIP", "PERFORMER", "LEAD_ORGANIZER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var grantType: EntryRole = EntryRole.CANNOT_ATTEND,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Beengedés engedélyezve")
    @property:GenerateOverview(columnName = "Engedélyezve", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false)
    var allowed: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 8, label = "Token")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var token: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 9, label = "Válasz")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var response: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 10, label = "Beléptető ID-je", min = 0, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var gateUserId: Int = 0,

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "AdmissionEntry",
        view = "control/admission-entries",
        showPermission = StaffPermissions.PERMISSION_SHOW_ADMISSIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as FormEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()
    override fun toString(): String {
        return "AdmissionEntryEntity(id=$id, userName='$userName', timestamp=$timestamp)"
    }

}
