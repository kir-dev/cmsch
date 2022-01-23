package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.INPUT_TYPE_HIDDEN
import hu.bme.sch.cmsch.dto.Edit
import javax.persistence.*

@Entity
@Table(name="realtimeConfig")
data class RealtimeConfigEntity(
        @Id
        @GeneratedValue
        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(maxLength = 255, order = 1, label = "Kulcs", note = "A kulcs kis/nagybetű érzékeny")
        @property:GenerateOverview(columnName = "Kulcs", order = 1)
        var key: String = "",

        @Lob
        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 2, label = "Érték")
        @property:GenerateOverview(columnName = "Érték", order = 2)
        var value: String = "",

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(order = 2, label = "Megjegyzés")
        @property:GenerateOverview(columnName = "Megjegyzés", order = 3)
        var comment: String = ""

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $key = $value"
    }
}
