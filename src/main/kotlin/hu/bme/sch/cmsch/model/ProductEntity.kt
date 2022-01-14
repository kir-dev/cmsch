package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

enum class ProductType {
    MERCH,
    FOOD,
    OTHER
}

@Entity
@Table(name="products")
data class ProductEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 64, order = 1, label = "Név")
        @property:GenerateOverview(columnName = "Név", order = 1)
        @property:ImportFormat(ignore = false, columnId = 0)
        var name: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 2, label = "Ár", min = 1, defaultValue = "100", note = "JMF-ben természetesen")
        @property:GenerateOverview(columnName = "Ár", order = 1, centered = true)
        @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
        var price: Int = 0,

        @Enumerated(EnumType.STRING)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 3, label = "Típus", source = [ "MERCH", "FOOD", "OTHER" ])
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_ENUM, enumSource = ProductType::class)
        var type: ProductType = ProductType.OTHER,

        @Lob
        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 4, label = "Termék leírása")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 3)
        var description: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_FILE, order = 5, label = "Kép a termékről")
        @property:GenerateOverview(visible = false)
        var imageUrl: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Elérhető")
        @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_BOOLEAN)
        var available: Boolean = false,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Látható")
        @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_BOOLEAN)
        var visible: Boolean = false,

        @Column(nullable = false, columnDefinition = "varchar(255) default 'payments'")
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(order = 8, label = "Material Ikon", note = "Innen kell kimásolni a nevét az ikonnak: https://fonts.google.com/icons")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 6)
        var materialIcon: String = "payments"

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}
