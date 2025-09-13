package hu.bme.sch.cmsch.component.event

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.opengraph.OpenGraphResource
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="events")
@ConditionalOnBean(EventComponent::class)
data class EventEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
            note = "Csupa nem ékezetes kisbetű és kötőjel megengedett. " +
                    "Oldal megosztása: https://BASE_URL/share/event/{URL}", interpreter = InputInterpreter.PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var url: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1, useForSearch = true)
    @property:ImportFormat
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 3, label = "Kategória")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var category: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 4, label = "Mikor lesz a program?")
    @property:GenerateOverview(visible = true, columnName = "Időpont", order = 2, renderer = OverviewType.DATE, useForSearch = false)
    @property:ImportFormat
    var timestampStart: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 5, label = "Meddig tart a program?")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var timestampEnd: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 7, label = "Helyszín")
    @property:GenerateOverview(visible = true, columnName = "Helyszín", order = 3, useForSearch = true)
    @property:ImportFormat
    var place: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT, order = 8, label = "Rövid leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var previewDescription: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 9, label = "Hosszú leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
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
    @property:GenerateInput(type = InputType.FILE, order = 12, label = "Előnézeti kép", fileId = "0", fileType = "image")
    @property:GenerateOverview(visible = false)
    var previewImageUrl: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.FILE, order = 13, label = "Teljes kép", fileId = "1", fileType = "image")
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
    @property:GenerateInput(type = InputType.TEXT, order = 16, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    override var ogDescription: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 17, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 4, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 18, label = "Minimum rang a megtekintéshez",
            note = "GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező",
            source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var minRole: RoleType = RoleType.GUEST,

): ManagedEntity, OpenGraphResource, Duplicatable {

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

    override fun duplicate(): EventEntity {
        return this.copy()
    }

}
