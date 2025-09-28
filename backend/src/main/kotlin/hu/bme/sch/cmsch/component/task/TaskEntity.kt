package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class TaskType {
    TEXT,
    IMAGE,
    BOTH,
    ONLY_PDF,
    ONLY_ZIP
}

enum class TaskFormat {
    NONE,
    TEXT,
    CODE,
    FORM
}

@Entity
@Table(name="tasks")
@ConditionalOnBean(TaskComponent::class)
data class TaskEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Feladat neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var title: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.NUMBER, min = 0, order = 2, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 2, centered = true)
    @property:ImportFormat
    var categoryId: Int = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 3, label = "Leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var description: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT, order = 4, label = "Beadandó formátum",
            note = "Ez a beadó mező mellett jelenik meg, külön a leírástól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var expectedResultDescription: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 17, label = "Mintamegoldás",
        note = "A leadási határidő után jelenik meg")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var solution: String = "",

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 5, label = "Típus",
        source = [ "TEXT", "IMAGE", "BOTH", "ONLY_PDF", "ONLY_ZIP" ],
        note = "Mit tároljon el a szerver? A BOTH az szöveg és kép is. A PDF csak önmagában használható.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var type: TaskType = TaskType.TEXT,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 6, label = "Formátum",
        source = [ "NONE", "TEXT", "CODE", "FORM" ],
        note = "Mi legyen a beadás módja. NONE: személyes beadás, TEXT: egy soros form vagy fájl tallózó, CODE: kód editor, FORM: lásd lejjebb a formátumot")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var format: TaskFormat = TaskFormat.NONE,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT, order = 7, label = "Formátum leírása",
        note = "Ha a formátum FORM akkor ide kell beírni a form jsonját. " +
                "Formátum: [{\"title\":\"\",\"type\":\"number|text|textarea\",\"suffix\":\"\"}]")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var formatDescriptor: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 8, label = "Beadható ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 9, label = "Beadható eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 4, renderer = OverviewType.DATE)
    @property:ImportFormat
    var availableTo: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.NUMBER, order = 10, label = "Max pont")
    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "Max pont", order = 5, centered = true)
    @property:ImportFormat
    var maxScore: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 11, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 6, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 12, label = "Kiemelt",
            note = "Olyan szöveggel jelenik meg, hogy hamarosan lejár")
    @property:GenerateOverview(columnName = "Kiemelt", order = 7, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var highlighted: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = InputType.NUMBER, order = 13, label = "Sorrend")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var order: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.TEXT, order = 14, label = "Címke", note = "Ha nem tudod mi ez, hagyd üresen!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var tag: String = "",

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'BASIC'")
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 15,
        label = "Minimum rang a megtekintéshez",
        defaultValue = "BASIC",
        note = "A ranggal rendelkező már megtekintheti (BASIC = belépett, STAFF = rendező)",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var minRole: RoleType = RoleType.BASIC,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'SUPERUSER'")
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 16,
        label = "Maximum rang a megtekintéshez",
        defaultValue = "SUPERUSER",
        note = "A ranggal rendelkező még megtekintheti (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező)",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var maxRole: RoleType = RoleType.SUPERUSER,

): ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Task",
        view = "control/task",
        showPermission = StaffPermissions.PERMISSION_SHOW_TASKS
    )

    override fun toString(): String {
        return "[$id]: $title"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun duplicate(): TaskEntity {
        return this.copy()
    }

}
