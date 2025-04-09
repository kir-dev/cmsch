package hu.bme.sch.cmsch.component.serviceaccount

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.ImplicitPermissions
import jakarta.persistence.*
import org.springframework.core.env.Environment

@Entity
@Table(
    name = "serviceAccountKey",
    uniqueConstraints = [UniqueConstraint(columnNames = ["secretKey"])],
    indexes = [Index(columnList = "userId,validUntil,secretKey")]
)
data class ServiceAccountKeyEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(
        type = INPUT_TYPE_NUMBER, order = 1, label = "Felhasználó azonosítója", defaultValue = "0",
        note = "A fiók azonosítója, amihez a kulcs tartozik, csak akkor működik, ha a felhasználó service account"
    )
    @property:GenerateOverview(visible = true, columnName = "Felhasználó azonosító", renderer = OVERVIEW_TYPE_NUMBER)
    @property:ImportFormat
    var userId: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false)
    @property:GenerateInput(
        maxLength = 255, order = 2, label = "Kulcs", interpreter = INTERPRETER_PATH,
        note = "Ezt kell megadni az x-cmsch-service-account-key headerben (openssl rand -hex 32)"
    )
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var secretKey: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(
        type = INPUT_TYPE_DATE, order = 3, label = "Meddig valid",
        note = "Eddig az időpontig lehet használni ezt a kulcsot", defaultValue = "0"
    )
    @property:GenerateOverview(visible = true, columnName = "Eddig érvényes", renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat
    var validUntil: Long = 0,
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Service Account Keys",
        view = "control/service-account-key",
        showPermission = ImplicitPermissions.PERMISSION_SUPERUSER_ONLY
    )

}
