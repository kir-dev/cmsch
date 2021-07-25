package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="news")
data class NewsEntity(
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
    var title: String = "",

    @Lob
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Hír szövege")
    @property:GenerateOverview(visible = false)
    var content: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 2, label = "Rövid összefoglaló",
            note = "Ez a szöveg fog a főoldalon megjelenni", maxLength = 255)
    @property:GenerateOverview(visible = false)
    var brief: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 4, label = "Kép a hír mellé")
    @property:GenerateOverview(visible = false)
    var imageUrl: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Látható a hír")
    @property:GenerateOverview(columnName = "Látható", order = 2, centered = true)
    var visible: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Kiemelt hír")
    @property:GenerateOverview(columnName = "Kiemelt", order = 3, centered = true)
    var highlighted: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, order = 7, visible = false, interpreter = "timestamp")
    @property:GenerateOverview(visible = false)
    var timestamp: Long = 0,

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 8, label = "OG:Title")
    @property:GenerateOverview(visible = false)
    var ogTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 9, label = "OG:Image")
    @property:GenerateOverview(visible = false)
    var ogImage: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 10, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    var ogDescription: String = ""

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }
}