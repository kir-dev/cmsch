package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

@Entity
@Table(name="tokens")
data class TokenEntity(

    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Token neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Token")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    var token: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Beolvasható-e a token")
    @property:GenerateOverview(columnName = "Olvasható", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 4, label = "Típus")
    @property:GenerateOverview(columnName = "Típus", order = 3)
    @property:ImportFormat(ignore = false, columnId = 2)
    var type: String = "",

): ManagedEntity {

    override fun toString(): String {
        return "[$id] $title"
    }

}
