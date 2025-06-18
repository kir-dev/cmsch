package hu.bme.sch.cmsch.component.sheets

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="sheets")
@ConditionalOnBean(SheetsComponent::class)
class SheetsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Név", note = "Név a könnyű beazonosításhoz", maxLength = 255)
    @property:GenerateOverview(order = 1, columnName = "Név")
    @property:ImportFormat
    var name: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Token", note = "Ezt fogja használni synchez", maxLength = 255)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var token: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Url", note = "A deployment url-je: https://script.google.com/macros/s/.../exec", maxLength = 255)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var url: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Űrlap Id-je", type = InputType.NUMBER, note = "Melyik form triggerelje. Ha 0-akkor nem fogja egyik sem.", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var formTrigger: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 5, label = "Aktív", type = InputType.SWITCH, note = "Csak akkor triggerel ha ez be van kapcsolva")
    @property:GenerateOverview(order = 2, columnName = "Aktív", centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var enabled: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Sheets",
        view = "control/sheets",
        showPermission = StaffPermissions.PERMISSION_SHOW_SHEETS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NewsEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "id = $id, name = $name"
    }

}
