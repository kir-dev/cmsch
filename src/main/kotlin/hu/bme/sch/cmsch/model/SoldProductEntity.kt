package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import javax.persistence.*

@Entity
@Table(name="soldProducts")
data class SoldProductEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = true, columnName = "ID", centered = true)
        @property:ImportFormat(ignore = false, columnId = 0)
        override var id: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(order = 1, label = "Termék", enabled = false, ignore = true)
        @property:GenerateOverview(columnName = "Termék", order = 1)
        @property:ImportFormat(ignore = false, columnId = 1)
        var product: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(order = 2, label = "Ár", enabled = false, ignore = true, note = "Az árak JMF-bne értendőek")
        @property:GenerateOverview(columnName = "Ár", order = 2)
        @property:ImportFormat(ignore = false, columnId = 2)
        var price: Int = 0,

        @Column(nullable = true)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 3)
        var sellerId: Int? = null,

        @Column(nullable = false)
        @property:GenerateInput(order = 3, label = "Eladó neve", enabled = false, ignore = true)
        @property:ImportFormat(ignore = false, columnId = 4)
        var sellerName: String = "",

        @Column(nullable = false)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 5)
        var ownerId: Int = 0,

        @Column(nullable = false)
        @property:GenerateInput(order = 4, label = "Vevő neve", enabled = false, ignore = true)
        @property:GenerateOverview(columnName = "Vevő", order = 3)
        @property:ImportFormat(ignore = false, columnId = 6)
        var ownerName: String = "",

        @Column(nullable = false)
        @property:ImportFormat(ignore = false, columnId = 7)
        var responsibleGroupId: Int = 0,

        @Column(nullable = true)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 8)
        var responsibleId: Int? = null,

        @Column(nullable = false)
        @property:GenerateOverview(columnName = "Kezelő neve", order = 4)
        @property:ImportFormat(ignore = false, columnId = 9)
        var responsibleName: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Átadva", minimumRole = RoleType.ADMIN)
        @property:ImportFormat(ignore = false, columnId = 10)
        var shipped: Boolean = false,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Átadva ekkor", enabled = false, ignore = true)
        @property:ImportFormat(ignore = false, columnId = 11)
        var shippedAt: Long = 0,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Fizetve", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(columnName = "Fizetve", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 12)
        var payed: Boolean = false,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 8, label = "Kifizetve ekkor", enabled = false, ignore = true)
        @property:ImportFormat(ignore = false, columnId = 13)
        var payedAt: Long = 0,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Lezárva", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(columnName = "Lezárva", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 14)
        var finsihed: Boolean = false,

        @Lob
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 10, label = "Napló", enabled = false, ignore = true)
        @property:ImportFormat(ignore = false, columnId = 15)
        var log: String = "",

        @Column(nullable = false, columnDefinition = "varchar(255) default 'payments'")
        @property:GenerateInput(order = 11, label = "Material Icon")
        @property:ImportFormat(ignore = false, columnId = 16)
        var materialIcon: String = "",

): ManagedEntity {
        override fun toString(): String {
                return "[$id]: $product ${price}JMF ${ownerName}"
        }
}
