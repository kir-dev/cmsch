package hu.bme.sch.cmsch.component.proto

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="protos")
@ConditionalOnBean(ProtoComponent::class)
data class ProtoEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 1, label = "Útvonal",
        note = "A /api/proto/{ÚTVONAL} címen lesz elérhető. " +
                "Regex és minták nem támogatottak. " +
                "Per \"/\" jellel kell kezdődjön.", defaultValue = "/")
    @property:GenerateOverview(columnName = "Útvonal", order = 1)
    @property:ImportFormat(ignore = false)
    var path: String = "",

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 2, label = "Válasz",
        note = "Ez a válasz jelenik meg a megadott útvonalon.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var responseValue: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 3, label = "Mime type",
        note = "A válasz mime type-ja. Pl.: application/json", defaultValue = "application/json")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var mimeType: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 4, label = "HTTP code",
        note = "HTTP válasz kódja", defaultValue = "200")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, type = IMPORT_INT)
    var statusCode: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Aktív")
    @property:GenerateOverview(columnName = "Aktív", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, type = IMPORT_BOOLEAN)
    var enabled: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Proto",
        view = "control/proto",
        showPermission = StaffPermissions.PERMISSION_SHOW_PROTO
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ProtoEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "id = $id, path = $path"
    }

}
