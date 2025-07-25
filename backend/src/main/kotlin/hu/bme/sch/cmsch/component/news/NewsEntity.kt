package hu.bme.sch.cmsch.component.news

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
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="news")
@ConditionalOnBean(NewsComponent::class)
data class NewsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
        note = "Csupa nem ékezetes kisbetű és kötőjel megengedett. " +
                "Oldal megosztása: https://BASE_URL/share/news/{URL}", interpreter = InputInterpreter.PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var url: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 3, label = "Rövid tartalom",
        note = "Ez a hír összesítésben megjelenő tartalma")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var briefContent: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 3, label = "Tartalom",
            note = "Ez a hír teljes tartalma")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var content: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.FILE, order = 4, label = "Kép a hír mellé", fileType = "image")
    @property:GenerateOverview(visible = false)
    var imageUrl: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 5, label = "Látható a hír")
    @property:GenerateOverview(columnName = "Látható", order = 2, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 6, label = "Kiemelt hír")
    @property:GenerateOverview(columnName = "Kiemelt", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var highlighted: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 7, label = "Publikálás időpontja",
        note = "Az időpont előtt nem látszódik. Alkalmas időzítésre.", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var timestamp: Long = 0,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 8, label = "Minimum rang a megtekintéshez",
            note = "GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező ",
            source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var minRole: RoleType = RoleType.GUEST,

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 9, label = "OG:Title")
    @property:GenerateOverview(visible = false)
    override var ogTitle: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 10, label = "OG:Image")
    @property:GenerateOverview(visible = false)
    override var ogImage: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.TEXT, order = 11, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    override var ogDescription: String = "",

): ManagedEntity, OpenGraphResource {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "News",
        view = "control/news",
        showPermission = StaffPermissions.PERMISSION_SHOW_NEWS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NewsEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "id = $id, title = $title"
    }
}
