package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*
import kotlin.math.min

@Entity
@Table(name="achievementCategories")
data class AchievementCategoryEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 64, order = 1, label = "Kategória neve")
        @property:GenerateOverview(columnName = "Név", order = 1)
        @property:ImportFormat(ignore = false, columnId = 0)
        var name: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Kategória id-je")
        @property:GenerateOverview(columnName = "ID", order = 2)
        @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
        var categoryId: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 3, label = "Beadhatóak ekkortól")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_LONG)
        var availableFrom: Long = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Beadhatóak eddig")
        @property:GenerateOverview(columnName = "Eddig", order = 3, renderer = OVERVIEW_TYPE_DATE)
        @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_LONG)
        var availableTo: Long = 0,

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}