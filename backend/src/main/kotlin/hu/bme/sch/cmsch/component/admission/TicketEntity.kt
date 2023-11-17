package hu.bme.sch.cmsch.component.admission

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "tickets")
@ConditionalOnBean(AdmissionComponent::class)
data class TicketEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Tulajdonos")
    @property:GenerateOverview(columnName = "Tulajdonos", order = 1)
    @property:ImportFormat(ignore = false)
    var owner: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Email")
    @property:GenerateOverview(columnName = "Email", order = 2)
    @property:ImportFormat(ignore = false)
    var email: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Profil QR kód használata")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var useCmschId: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 4, label = "QR")
    @property:GenerateOverview(columnName = "QR", order = 3)
    @property:ImportFormat(ignore = false)
    var qrCode: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5,
        label = "Belépési jogosultság",
        source = [ "BANNED", "CANNOT_ATTEND", "USER", "ORGANIZER", "VIP", "PERFORMER", "LEAD_ORGANIZER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var grantType: EntryRole = EntryRole.CANNOT_ATTEND,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 6, label = "Megjegyzés")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var comment: String = "",
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TicketEntry",
        view = "control/tickets",
        showPermission = StaffPermissions.PERMISSION_SHOW_ADMISSIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TicketEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "TicketEntity(id=$id, owner='$owner', email='$email', useCmschId=$useCmschId, qrCode='$qrCode', grantType=$grantType, comment='$comment')"
    }

}
