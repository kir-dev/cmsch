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

enum class TaskCategoryType {
    REGULAR,
    PROFILE_REQUIRED,
}

@Entity
@Table(name="taskCategories")
@ConditionalOnBean(TaskComponent::class)
data class TaskCategoryEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Kategória neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.NUMBER, min = 0, order = 2, label = "Kategória id-je. " +
            "Egyedinek kell lennie, különben összeakad a rendszer!")
    @property:GenerateOverview(columnName = "ID", order = 2)
    @property:ImportFormat
    var categoryId: Int = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    @ColumnDefault("''")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 3, label = "Leírás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var description: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.DATE, order = 4, label = "Beadhatóak ekkortól", defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.DATE, order = 5, label = "Beadhatóak eddig", defaultValue = "0")
    @property:GenerateOverview(columnName = "Eddig", order = 3, renderer = OverviewType.DATE)
    @property:ImportFormat
    var availableTo: Long = 0,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 6, label = "Típus", source = [ "REGULAR", "PROFILE_REQUIRED" ],
        note = "A PROFILE_REQUIRED olyan task ami a többi feladattól külön jelenik meg, és külön van mutatva a profil oldalon. " +
                "Ideális profilkép vagy motivációs levél feltöltéshez.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var type: TaskCategoryType = TaskCategoryType.REGULAR,

    @ColumnDefault("false")
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 7, label = "Hírdetett",
        note = "Külön kijelzésre kerülnek bizonyos helyeken az oldalon (pl. team komponens)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var advertised: Boolean = false,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @ColumnDefault("'BASIC'")
    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 8,
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
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 9,
        label = "Maximum rang a megtekintéshez",
        defaultValue = "SUPERUSER",
        note = "A ranggal rendelkező még megtekintheti (GUEST = kijelentkezett, BASIC = belépett, STAFF = rendező)",
        source = [ "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var maxRole: RoleType = RoleType.SUPERUSER,

): ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TaskCategory",
        view = "control/categories",
        showPermission = StaffPermissions.PERMISSION_SHOW_TASK_CATEGORIES
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TaskCategoryEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name)"
    }

    override fun duplicate(): TaskCategoryEntity {
        return this.copy()
    }

}
