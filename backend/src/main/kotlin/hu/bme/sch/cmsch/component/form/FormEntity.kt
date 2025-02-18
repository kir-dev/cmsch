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
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

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
    @property:ImportFormat
    var name: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Url",
        note = "Csupa nem ékezetes kisbetű és kötőjel megengedett", interpreter = INTERPRETER_PATH)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var url: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 3, label = "Menüben megjelenő neve",
        note = "Csak akkor szükséges ha menüből lesz megnyitható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var menuName: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = INPUT_TYPE_FORM_EDITOR, order = 100, label = "Kitöltendő űrlap", defaultValue = "[]")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var formJson: String = "[]",

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5,
        label = "Minimum rang a megtekintéshez",
        note = "A ranggal rendelkező már megtekintheti (BASIC = belépett, STAFF = rendező)",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var minRole: RoleType = RoleType.BASIC,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6,
        label = "Maximum rang a megtekintéshez",
        note = "A ranggal rendelkező még megtekintheti (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező)",
        source = [ "GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var maxRole: RoleType = RoleType.SUPERUSER,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 9, label = "Sikeres leadás utáni üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var submittedMessage: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 10, label = "Elfogadás utáni üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var acceptedMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "ATTENDEE jog automatikusan",
        note = "Automatikus ATTENDEE jog adása sikeres kitöltésért")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var grantAttendeeRole: Boolean = false,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 12, label = "Kitölthető innentől", defaultValue = "0")
    @property:GenerateOverview(columnName = "Ettől", order = 2, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat
    var availableFrom: Long = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 13, label = "Kitölthető eddig", defaultValue = "0")
    @property:GenerateOverview(columnName = "Eddig", order = 3, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat
    var availableUntil: Long = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 3, label = "Kitölthető-e",
        note = "Ha be van kapcsolva és az idő intervallum is megfelel, akkor lehet beküldeni")
    @property:GenerateOverview(columnName = "Kitölthető", order = 4, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat
    var open: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 15, label = "Maximum kitöltés",
        note = "Ennyi ember töltheti ki maximum. (-1 = végtelen)", min = -1, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var submissionLimit: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 16, label = "BME jegy integráció",
        note = "Ha be van kapcsolva, akkor elfogad emailcímeket a BME jegy integrációból")
    @property:GenerateOverview(columnName = "BME Jegy", order = 5, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat
    var selected: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 17, label = "Csapatra korlátozás",
        note = "Csak ezek a csoportok tölthetik ki, ha üres akkor mindenki")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var allowedGroups: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 18, label = "Csoport tagság miatt eltiltva üzenet")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var groupRejectedMessage: String = "",

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("false")
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 19, label = "E-mail küldése a kitöltés után",
        note = "Ha be van kapcsolva, a megadott címre E-mailt küld")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var sendConfirmationEmail: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("false")
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 20, label = "E-mail küldése csak egyszer",
        note = "Ha be van kapcsolva, akkor csak első kitöltés után küld E-mail, szerkesztéskor nem")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var sendConfirmationEmailOnlyOnce: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @ColumnDefault("''")
    @property:GenerateInput(maxLength = 128, order = 21, label = "E-mail mező neve",
        note = "A mezőnek a neve, ami értékére az E-mailt küldje (MEZŐ NEVE), " +
                "ha üres, akkor a felhasználóét használja, ha nincs a felhasználónak E-mailje, akkor pedig nem küld :(")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var emailFieldName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @ColumnDefault("''")
    @property:GenerateInput(maxLength = 128, order = 22, label = "E-mail sablon hivatkozása",
        note = "A kiküldendő E-mail sablon hivatkozási neve")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var emailTemplateSelector: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @ColumnDefault("''")
    @property:GenerateInput(maxLength = 128, order = 23, label = "Beléptető token mező neve",
        note = "Olyan jegyekhez lehet használni, ahol nem kell belépni a felhasználónak a form kitöltéséhez." +
                "Olyan mezőt adj meg, ami INJECT_RANDOM_TOKEN típusú (MEZŐ NEVE). " +
                "Ez a mező ne legyen módosítható -> FONTOS!!!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var tokenFieldName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 7, label = "Csoport a birtokos",
        note = "Ha be van kapcsolva, akkor a csoport a beadások birtokosa, ha ki an kapcsolva, akkor felhasználók")
    @property:GenerateOverview(columnName = "Csoportos", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat
    var ownerIsGroup: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @ColumnDefault("false")
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 8, label = "Hírdetett",
        note = "Ha be van kapcsolva, akkor bizonyos oldalakon megjelenik mint kitöltendő form")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
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
