package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.opengraph.OpenGraphResource
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="events")
@ConditionalOnBean(EventComponent::class)
data class EventEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
            note = "Csupa nem ékezetes kisbetű és kötőjel megengedett. " +
                    "Oldal megosztása: https://BASE_URL/share/event/{URL}", interpreter = INTERPRETER_PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    var url: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1, useForSearch = true)
    @property:ImportFormat(ignore = false, columnId = 1)
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 3, label = "Kategória")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var category: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Mikor lesz a program?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_LONG)
    var timestampStart: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 5, label = "Meddig tart a program?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_LONG)
    var timestampEnd: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 7, label = "Helyszín")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5)
    var place: String = "",

    @Lob
    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 8, label = "Rövid leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var previewDescription: String = "",

    @Lob
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 9, label = "Hosszú leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var description: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 10, label = "Extra gomb szöveg", note = "Üres szöveg = nem jelenik meg a gomb")
    @property:GenerateOverview(visible = false)
    var extraButtonTitle: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 11, label = "Extra gomb URL")
    @property:GenerateOverview(visible = false)
    var extraButtonUrl: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 12, label = "Előnézeti kép", fileId = "0", fileType = "image")
    @property:GenerateOverview(visible = false)
    var previewImageUrl: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 13, label = "Teljes kép", fileId = "1", fileType = "image")
    @property:GenerateOverview(visible = false)
    var fullImageUrl: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 14, label = "OG:Title")
    @property:GenerateOverview(visible = false)
    override var ogTitle: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 15, label = "OG:Image")
    @property:GenerateOverview(visible = false)
    override var ogImage: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 16, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    override var ogDescription: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 17, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 18, label = "Minimum rang a megtekintéshez",
            note = "GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező",
            source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_ENUM, enumSource = RoleType::class)
    var minRole: RoleType = RoleType.GUEST,

): ManagedEntity, OpenGraphResource {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Event",
        view = "control/events",
        showPermission = StaffPermissions.PERMISSION_SHOW_EVENTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as EventEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
