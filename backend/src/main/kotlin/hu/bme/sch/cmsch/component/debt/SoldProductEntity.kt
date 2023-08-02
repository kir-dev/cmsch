package hu.bme.sch.cmsch.component.debt

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="soldProducts")
@ConditionalOnBean(DebtComponent::class)
data class SoldProductEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    @property:ImportFormat(ignore = false, columnId = 0)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 1, label = "Termék", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Termék", order = 1)
    @property:ImportFormat(ignore = false, columnId = 1)
    var product: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(order = 2, label = "Ár", enabled = false, ignore = true, note = "Az árak JMF-ben értendőek")
    @property:GenerateOverview(columnName = "Ár", order = 2)
    @property:ImportFormat(ignore = false, columnId = 2)
    var price: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3)
    var sellerId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Eladó neve", enabled = false, ignore = true)
    @property:ImportFormat(ignore = false, columnId = 4)
    var sellerName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5)
    var ownerId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "Vevő neve", enabled = false, ignore = true)
    @property:GenerateOverview(columnName = "Vevő", order = 3)
    @property:ImportFormat(ignore = false, columnId = 6)
    var ownerName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var responsibleGroupId: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:GenerateInput(visible = false, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8)
    var responsibleId: Int? = null,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateOverview(columnName = "Kezelő neve", order = 4)
    @property:ImportFormat(ignore = false, columnId = 9)
    var responsibleName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Átadva")
    @property:ImportFormat(ignore = false, columnId = 10)
    var shipped: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Átadva ekkor", enabled = false, ignore = true)
    @property:ImportFormat(ignore = false, columnId = 11)
    var shippedAt: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Fizetve")
    @property:GenerateOverview(columnName = "Fizetve", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 12)
    var payed: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 8, label = "Kifizetve ekkor", enabled = false, ignore = true)
    @property:ImportFormat(ignore = false, columnId = 13)
    var payedAt: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Lezárva")
    @property:GenerateOverview(columnName = "Lezárva", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 14)
    var finsihed: Boolean = false,

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 10, label = "Napló", enabled = false, ignore = true)
    @property:ImportFormat(ignore = false, columnId = 15)
    var log: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "varchar(255) default 'payments'")
    @property:GenerateInput(order = 11, label = "Material Icon")
    @property:ImportFormat(ignore = false, columnId = 16)
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
