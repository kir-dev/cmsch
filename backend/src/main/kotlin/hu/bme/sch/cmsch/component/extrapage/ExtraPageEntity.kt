package hu.bme.sch.cmsch.component.extrapage

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.opengraph.OpenGraphResource
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.persistence.*

@Entity
@Table(name="extraPages")
@ConditionalOnBean(ExtraPageComponent::class)
data class ExtraPageEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
            note = "Csupa nem ékezetes kisbetű és kötőjel megegengedett. " +
                    "Oldal megosztása: https://BASE_URL/share/event/{URL}", interpreter = INTERPRETER_PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 0)
    var url: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false, columnId = 1)
    var title: String = "",

    @Lob
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 3, label = "Az oldal tartalma")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var content: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Látható", note = "Listázott az oldal")
    @property:GenerateOverview(columnName = "Látható", order = 2, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 5, label = "Elérhető", note = "Meg lehet nyitni az oldalt")
    @property:GenerateOverview(columnName = "Elérhető", order = 3, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_BOOLEAN)
    var open: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 6, label = "Jog a szerkesztéshez", minimumRole = RoleType.ADMIN,
        note = "pl.: EXTRAPAGE_EDIT_CONTACTS alapértelmezett: EXTRAPAGE_EDIT (ezt mindenki tudja szerkeszteni aki látja a menüt is)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5)
    var permissionToEdit: String = "EXTRAPAGE_EDIT",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Látható a menüben")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_BOOLEAN)
    var showAsMenu: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(maxLength = 64, order = 8, label = "Menü cím")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var menuTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 9, label = "Minimum jogkör",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ], minimumRole = RoleType.ADMIN,
        note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_ENUM, enumSource = RoleType::class)
    var minRole: RoleType = RoleType.GUEST,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 10, label = "OG:Title")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 9)
    override var ogTitle: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(order = 11, label = "OG:Image")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 10)
    override var ogImage: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 12, label = "OG:Description")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 11)
    override var ogDescription: String = ""

): ManagedEntity, OpenGraphResource {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ExtraPageEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
