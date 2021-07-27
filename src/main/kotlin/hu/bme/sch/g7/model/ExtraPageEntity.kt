package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import javax.persistence.*

@Entity
@Table(name="extraPages")
data class ExtraPageEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
            note = "Csupa nem ékezetes kisbetű és kötőjel megegengedett", interpreter = "path")
    @property:GenerateOverview(visible = false)
    var url: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    var title: String = "",

    @Lob
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Termék leírása")
    @property:GenerateOverview(visible = false)
    var content: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Látható", note = "Listázott az oldal")
    @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true)
    var visible: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Elérhető", note = "Meg lehet nyitni az oldalt")
    @property:GenerateOverview(columnName = "Elérhető", order = 2, centered = true)
    var open: Boolean = false,

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
    var ogDescription: String = ""

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }
}