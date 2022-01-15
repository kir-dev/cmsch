package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

@Entity
@Table(name="riddleCategories")
data class RiddleCategoryEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var categoryId: Int = 0,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Látható-e a riddle kategória")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 4, label = "Minimum rang",
        note = "GUEST = kijelentkezett, BASIC = gólya, STAFF = rendező ",
        source = [ "GUEST", "BASIC", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_ENUM, enumSource = RoleType::class)
    var minRole: RoleType = RoleType.GUEST

) : ManagedEntity {
    override fun toString(): String {
        return "[$id] $title"
    }
}
