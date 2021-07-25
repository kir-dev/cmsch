package hu.bme.sch.g7.model

import hu.bme.sch.g7.admin.*
import javax.persistence.*

@Entity
@Table(name="products")
data class ProductEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 2, label = "Ár")
    @property:GenerateOverview(columnName = "Ár", order = 1)
    var price: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Termék leírása")
    @property:GenerateOverview(visible = false)
    var description: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 4, label = "Kép a termékről")
    @property:GenerateOverview(visible = false)
    var imageUrl: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Elérhető")
    @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true)
    var available: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true)
    var visible: Boolean = false

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}