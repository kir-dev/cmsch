package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class TaskType {
    TEXT,
    IMAGE,
    BOTH,
    ONLY_PDF
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
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Feladat neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var categoryId: Int = 0,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 3, label = "Leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_LOB)
    var description: String = "",

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 4, label = "Beadandó formátum",
            note = "Ez a beadó mező mellett jelenik meg, külön a leírástól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_LOB)
    var expectedResultDescription: String = "",

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT_MARKDOWN, order = 15, label = "Mintamegoldás",
        note = "A leadási határidő után jelenik meg")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 14, type = IMPORT_LOB)
    var solution: String = "",

    @Enumerated(EnumType.STRING)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Típus",
        source = [ "TEXT", "IMAGE", "BOTH", "ONLY_PDF" ],
        note = "Mit tároljon el a szerver? A BOTH az szöveg és kép is. A PDF csak önmagában használható.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_ENUM, enumSource = TaskType::class)
    var type: TaskType = TaskType.TEXT,

    @Enumerated(EnumType.STRING)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 6, label = "Formátum",
        source = [ "NONE", "TEXT", "CODE", "FORM" ],
        note = "Mi legyen a beadás módja. NONE: személyes beadás, TEXT: egy soros form vagy fájl tallózó, CODE: kód editor, FORM: lásd lejjebb a formátumot")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_ENUM, enumSource = TaskFormat::class)
    var format: TaskFormat = TaskFormat.NONE,

    @Lob
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 7, label = "Formátum leírása",
        note = "Ha a formátum FORM akkor ide kell beírni a form jsonját. " +
                "Formátum: [{\"title\":\"\",\"type\":\"number|text|textarea\",\"suffix\":\"\"}]")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6, type = IMPORT_LOB)
    var formatDescriptor: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 8, label = "Beadható ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 9, label = "Beadható eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 4, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 8, type = IMPORT_LONG)
    var availableTo: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 10, label = "Max pont")
    @property:GenerateOverview(columnName = "Max pont", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 9, type = IMPORT_INT)
    var maxScore: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 11, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 6, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 10, type = IMPORT_BOOLEAN)
    var visible: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 12, label = "Kiemelt",
            note = "Olyan szöveggel jelenik meg, hogy hamarosan lejár")
    @property:GenerateOverview(columnName = "Kiemelt", order = 7, centered = true, renderer = OVERVIEW_TYPE_BOOLEAN)
    @property:ImportFormat(ignore = false, columnId = 11, type = IMPORT_BOOLEAN)
    var highlighted: Boolean = false,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 13, label = "Sorrend")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 12, type = IMPORT_LONG)
    var order: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 14, label = "Címke", note = "Ha nem tudod mi ez, hagyd üresen!")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 13)
    var tag: String = "",

): ManagedEntity {

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
}
