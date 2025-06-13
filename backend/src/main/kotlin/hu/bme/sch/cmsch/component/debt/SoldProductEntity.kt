package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="soldProducts")
@ConditionalOnBean(DebtComponent::class)
data class SoldProductEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    @property:ImportFormat
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 1, label = "Termék", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Termék", order = 1)
    @property:ImportFormat
    var product: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Ár", enabled = false, ignore = true, note = "Az árak JMF-ben értendőek")
    @property:GenerateOverview(columnName = "Ár", order = 2)
    @property:ImportFormat
    var price: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var sellerId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Eladó neve", enabled = false, ignore = true)
    @property:ImportFormat
    var sellerName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var ownerId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Vevő neve", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Vevő", order = 3)
    @property:ImportFormat
    var ownerName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:ImportFormat
    var responsibleGroupId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var responsibleId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateOverview(columnName = "Kezelő neve", order = 4)
    @property:ImportFormat
    var responsibleName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Átadva")
    @property:ImportFormat
    var shipped: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Átadva ekkor", enabled = false, ignore = true)
    @property:ImportFormat
    var shippedAt: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Fizetve")
    @property:GenerateOverview(columnName = "Fizetve", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat
    var payed: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 8, label = "Kifizetve ekkor", enabled = false, ignore = true)
    @property:ImportFormat
    var payedAt: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Lezárva")
    @property:GenerateOverview(columnName = "Lezárva", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat
    var finsihed: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 10, label = "Napló", enabled = false, ignore = true)
    @property:ImportFormat
    var log: String = "",

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("'payments'")
    @Column(nullable = false, length = 255)
    @property:GenerateInput(order = 11, label = "Material Icon")
    @property:ImportFormat
    var materialIcon: String = "",

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "SoldProduct",
        view = "control/debts",
        showPermission = StaffPermissions.PERMISSION_SHOW_DEBTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SoldProductEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
