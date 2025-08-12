package hu.bme.sch.cmsch.component.race

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="raceCategories")
@ConditionalOnBean(RaceComponent::class)
data class RaceCategoryEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var name: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 2, label = "Slug (url)",
        note = "Csupa nem ékezetes kisbetű és kötőjel megengedett.", interpreter = InputInterpreter.PATH)
    @property:GenerateOverview(columnName = "Slug", order = 1)
    @property:ImportFormat
    var slug: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT_MARKDOWN, order = 3, label = "Leírás",
        note = "Meg fog jelenni a kategória menüjének tetején")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var description: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 4, label = "Látható-e a kategória")
    @property:GenerateOverview(columnName = "Látható", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false

): ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "RaceCategory",
        view = "control/race-categories",
        showPermission = StaffPermissions.PERMISSION_SHOW_RACE_CATEGORY
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RaceCategoryEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = $name, slug = $slug)"
    }

    override fun duplicate(): RaceCategoryEntity {
        return this.copy()
    }

}
