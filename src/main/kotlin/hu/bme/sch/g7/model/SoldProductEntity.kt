package hu.bme.sch.g7.model

import hu.bme.sch.g7.admin.*
import javax.persistence.*

@Entity
@Table(name="soldProducts")
data class SoldProductEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @Column(nullable = false)
        @property:GenerateInput(order = 1, label = "Termék", enabled = false, ignore = true)
        @property:GenerateOverview(columnName = "Termék", order = 1)
        var product: String = "",

        @Column(nullable = false)
        @property:GenerateInput(order = 2, label = "Ár", enabled = false, ignore = true, note = "Az árak JMF-bne értendőek")
        @property:GenerateOverview(columnName = "Ár", order = 2)
        var price: Int = 0,

        @Column(nullable = true)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        var sellerId: Int? = null,

        @Column(nullable = false)
        @property:GenerateInput(order = 3, label = "Eladó neve", enabled = false, ignore = true)
        var sellerName: String = "",

        @Column(nullable = false)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        var ownerId: Int = 0,

        @Column(nullable = false)
        @property:GenerateInput(order = 4, label = "Vevő neve", enabled = false, ignore = true)
        @property:GenerateOverview(columnName = "Vevő", order = 3)
        var ownerName: String = "",

        @Column(nullable = false)
        var responsibleGroupId: Int = 0,

        @Column(nullable = true)
        @property:GenerateInput(visible = false, ignore = true)
        @property:GenerateOverview(visible = false)
        var responsibleId: Int? = null,

        @Column(nullable = false)
        @property:GenerateOverview(columnName = "Kezelő neve", order = 4)
        var responsibleName: String = "",

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Átadva", minimumRole = RoleType.ADMIN)
        var shipped: Boolean = false,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 6, label = "Átadva ekkor", enabled = false, ignore = true)
        var shippedAt: Long = 0,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Fizetve", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(columnName = "Fizetve", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        var payed: Boolean = false,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 8, label = "Kifizetve ekkor", enabled = false, ignore = true)
        var payedAt: Long = 0,

        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "Lezárva", minimumRole = RoleType.ADMIN)
        @property:GenerateOverview(columnName = "Lezárva", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        var finsihed: Boolean = false,

        @Lob
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 10, label = "Napló", enabled = false, ignore = true)
        var log: String = ""

): ManagedEntity {
        override fun toString(): String {
                return "[$id]: $product ${price}JMF ${ownerName}"
        }
}