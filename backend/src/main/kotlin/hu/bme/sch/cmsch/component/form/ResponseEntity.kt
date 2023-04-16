package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import javax.persistence.*

@Entity
@Table(name="formResponses")
@ConditionalOnBean(FormComponent::class)
data class ResponseEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    @property:ImportFormat(ignore = true)
    override var id: Int = 0,

    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0, type = IMPORT_INT)
    var submitterUserId: Int? = null,

    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Beküldő neve", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Beküldő", order = 1)
    @property:ImportFormat(ignore = false, columnId = 1)
    var submitterUserName: String = "",

    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
    var submitterGroupId: Int? = null,

    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Beküldő csoport", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "", order = 2)
    @property:ImportFormat(ignore = false, columnId = 3)
    var submitterGroupName: String = "",

    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    var formId: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Beküldve ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_LONG)
    var creationDate: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Utoljára módosítva", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LONG)
    var lastUpdatedDate: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Fizetve", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(columnName = "Fizetve", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_BOOLEAN)
    var accepted: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Fizetve ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_LONG)
    var acceptedAt: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Elutasítva", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(columnName = "Elutasítva", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_BOOLEAN)
    var rejected: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 255, order = 8, label = "Elutasítás indoka", note = "Csak akkor kell ha elutasított")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10)
    var rejectionMessage: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 255, order = 9, label = "Email",
        note = "A felhasználó email címe, ez a BME Jegy integrációhoz kell")
    @property:GenerateOverview(columnName = "Beküldő emailje", order = 2)
    @property:ImportFormat(ignore = false, columnId = 11)
    var email: String = "",

    @Lob
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 255, order = 10, label = "Kitöltés",
        note = "A kitöltés JSON formátumban", type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_LOB)
    var submission: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "Adatok elfogadva", minimumRole = RoleType.ADMIN)
    @property:GenerateOverview(columnName = "Adatok", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_BOOLEAN)
    var detailsValidated: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 12, label = "Adatok elfogadva ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_LONG)
    var detailsValidatedAt: Long = 0,

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

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
