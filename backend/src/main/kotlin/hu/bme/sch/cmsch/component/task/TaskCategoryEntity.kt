package hu.bme.sch.cmsch.component.task

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

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
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Kategória neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var name: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Kategória id-je. " +
            "Egyedinek kell lennie, különben összeakad a rendszer!")
    @property:GenerateOverview(columnName = "ID", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var categoryId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 3, label = "Beadhatóak ekkortól")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_LONG)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_DATE, order = 4, label = "Beadhatóak eddig")
    @property:GenerateOverview(columnName = "Eddig", order = 3, renderer = OVERVIEW_TYPE_DATE)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_LONG)
    var availableTo: Long = 0,

    @Enumerated(EnumType.STRING)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 5, label = "Típus", source = [ "REGULAR", "PROFILE_REQUIRED" ],
        note = "A PROFILE_REQUIRED olyan task ami a többi feladattól külön jelenik meg, és külön van mutatva a profil oldalon. " +
                "Ideális profilkép vagy motivációs levél feltöltéshez.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_ENUM, enumSource = TaskCategoryType::class)
    var type: TaskCategoryType = TaskCategoryType.REGULAR,

    @Column(nullable = false, columnDefinition = "BOOLEAN default false")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 6, label = "Hírdetett",
        note = "Külön kijelzésre kerülnek bizonyos helyeken az oldalon (pl. team komponens)")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var advertised: Boolean = false,

): ManagedEntity {

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
}
