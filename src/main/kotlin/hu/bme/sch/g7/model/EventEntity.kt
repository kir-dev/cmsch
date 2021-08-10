package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="events")
data class EventEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
            note = "Csupa nem ékezetes kisbetű és kötőjel megegengedett", interpreter = "path")
    @property:GenerateOverview(visible = false)
    var url: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    var title: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 3, label = "Kategória")
    @property:GenerateOverview(visible = false)
    var category: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Mikor lesz a program?")
    @property:GenerateOverview(visible = false)
    var timestampStart: Long = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 5, label = "Meddig tart a program?")
    @property:GenerateOverview(visible = false)
    var timestampEnd: Long = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 7, label = "Helyszín")
    @property:GenerateOverview(visible = false)
    var place: String = "",

    @Lob
    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 8, label = "Rövid leírás")
    @property:GenerateOverview(visible = false)
    var previewDescription: String = "",

    @Lob
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 9, label = "Hosszú leírás")
    @property:GenerateOverview(visible = false)
    var description: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 10, label = "Extra gomb szöveg", note = "Üres szöveg = nem jelenik meg a gomb")
    @property:GenerateOverview(visible = false)
    var extraButtonTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 11, label = "Extra gomb URL")
    @property:GenerateOverview(visible = false)
    var extraButtonUrl: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 12, label = "Előnézeti kép", fileId = "0")
    @property:GenerateOverview(visible = false)
    var previewImageUrl: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 13, label = "Teljes kép", fileId = "1")
    @property:GenerateOverview(visible = false)
    var fullImageUrl: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 14, label = "OG:Title")
    @property:GenerateOverview(visible = false)
    var ogTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 15, label = "OG:Image")
    @property:GenerateOverview(visible = false)
    var ogImage: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 16, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    var ogDescription: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 17, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    var visible: Boolean = false

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }
}