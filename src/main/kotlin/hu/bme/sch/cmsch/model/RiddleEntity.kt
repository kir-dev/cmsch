package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

@Entity
@Table(name="riddles")
data class RiddleEntity(

    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 2, label = "A képrejtvény", fileType = "image")
    @property:GenerateOverview(visible = false)
    var imageUrl: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 3, label = "Megoldás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1)
    var solution: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 4, label = "Hint")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var hint: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 5, label = "Pont")
    @property:GenerateOverview(columnName = "Pont", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    var score: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 6, label = "Sorrend")
    @property:GenerateOverview(columnName = "Sorrend", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_LONG)
    var order: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 7, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    var categoryId: Int = 0

) : ManagedEntity {
    override fun toString(): String {
        return "[$id] $title"
    }
}
