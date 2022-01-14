package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
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
        @property:ImportFormat(ignore = false, columnId = 0)
        var title: String = "",

        @Lob
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Hír szövege",
                note = "Ez egyelőre nincs használva")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 1)
        var content: String = "",

        @Lob
        @JsonView(value = [ Edit::class, Preview::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 2, label = "Rövid összefoglaló",
                note = "Ez a szöveg fog a főoldalon megjelenni")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 2)
        var brief: String = "",

        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_FILE, order = 4, label = "Kép a hír mellé", fileType = "image")
        @property:GenerateOverview(visible = false)
        var imageUrl: String = "",

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Látható a hír")
        @property:GenerateOverview(columnName = "Látható", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_BOOLEAN)
        var visible: Boolean = false,

        @JsonView(value = [ Edit::class, Preview::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Kiemelt hír")
        @property:GenerateOverview(columnName = "Kiemelt", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
        @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_BOOLEAN)
        var highlighted: Boolean = false,

        @JsonView(value = [ Edit::class, Preview::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_DATE, order = 7, label = "Publikálás időpontja", defaultValue = "0")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_LONG)
        var timestamp: Long = 0,

        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 9, label = "OG:Title", note = "Ez egyelőre nincs használva")
        @property:GenerateOverview(visible = false)
        var ogTitle: String = "",

        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 10, label = "OG:Image", note = "Ez egyelőre nincs használva")
        @property:GenerateOverview(visible = false)
        var ogImage: String = "",

        @JsonView(value = [ Edit::class, FullDetails::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 11, label = "OG:Description", note = "Ez egyelőre nincs használva")
        @property:GenerateOverview(visible = false)
        var ogDescription: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 8, label = "Minimum rang a megtekintéshez",
                note = "GUEST = kijelentkezett, BASIC = gólya, STAFF = senior ",
                source = [ "GUEST", "BASIC", "STAFF", "ADMIN", "SUPERUSER" ])
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_ENUM, enumSource = RoleType::class)
        var minRole: RoleType = RoleType.GUEST

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $title"
    }
}
