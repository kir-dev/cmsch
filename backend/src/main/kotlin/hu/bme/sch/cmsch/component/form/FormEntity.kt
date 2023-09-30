package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
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
@Table(name="forms")
@ConditionalOnBean(FormComponent::class)
data class FormEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Űrlap címe")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var name: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
        note = "Csupa nem ékezetes kisbetű és kötőjel megengedett", interpreter = INTERPRETER_PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1)
    var url: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 3, label = "Menüben megjelenő neve",
        note = "Csak akkor szükséges ha menüből lesz megnyitható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var menuName: String = "",

    @Lob
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FORM_EDITOR, order = 4, label = "Kitöltendő űrlap", defaultValue = "[]")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_LOB)
    var formJson: String = "[]",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5,
        label = "Minimum rang a megtekintéshez",
        note = "A ranggal rendelkező már megtekintheti (BASIC = belépett, STAFF = rendező)",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_ENUM, enumSource = RoleType::class)
    var minRole: RoleType = RoleType.BASIC,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6,
        label = "Maximum rang a megtekintéshez",
        note = "A ranggal rendelkező még megtekintheti (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező)",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_ENUM, enumSource = RoleType::class)
    var maxRole: RoleType = RoleType.SUPERUSER,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 7, label = "Sikeres leadás utáni üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LOB)
    var submittedMessage: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 8, label = "Elfogadás utáni üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_LOB)
    var acceptedMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 9, label = "ATTENDEE jog automatikusan",
        note = "Automatikus ATTENDEE jog adása sikeres kitöltésért")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_BOOLEAN)
    var grantAttendeeRole: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 10, label = "Kitölthető innentől", defaultValue = "0")
    @property:GenerateOverview(columnName = "Ettől", order = 2, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 11, label = "Kitölthető eddig", defaultValue = "0")
    @property:GenerateOverview(columnName = "Eddig", order = 3, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_LONG)
    var availableUntil: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 12, label = "Kitölthető-e",
        note = "Ha be van kapcsolva és az idő intervallum is megfelel, akkor lehet beküldeni")
    @property:GenerateOverview(columnName = "Kitölthető", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_BOOLEAN)
    var open: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 13, label = "Maximum kitöltés",
        note = "Ennyi ember töltheti ki maximum. (-1 = végtelen)", min = -1, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_INT)
    var submissionLimit: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 14, label = "BME jegy integráció",
        note = "Ha be van kapcsolva, akkor elfogad emailcímeket a BME jegy integrációból")
    @property:GenerateOverview(columnName = "BME Jegy", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 13, type = IMPORT_BOOLEAN)
    var selected: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 15, label = "Csapatra korlátozás",
        note = "Csak ezek a csoportok tölthetik ki, ha üres akkor mindenki")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14)
    var allowedGroups: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 16, label = "Csoport tagság miatt eltiltva üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 15, type = IMPORT_LOB)
    var groupRejectedMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 17, label = "Csoport a birtokos",
        note = "Ha be van kapcsolva, akkor a csoport a beadások birtokosa, ha ki an kapcsolva, akkor felhasználók")
    @property:GenerateOverview(columnName = "Csoportos", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 16, type = IMPORT_BOOLEAN)
    var ownerIsGroup: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, columnDefinition = "BOOLEAN default false")
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 18, label = "Hírdetett",
        note = "Ha be van kapcsolva, akkor bizonyos oldalakon megjelenik mint kitöltendő form")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var advertized: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Form",
        view = "control/forms",
        showPermission = StaffPermissions.PERMISSION_SHOW_FORM_RESULTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as FormEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "FormEntity(id=$id, name='$name', url='$url')"
    }

}
