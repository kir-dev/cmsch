package hu.bme.sch.cmsch.component.riddle

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

@Entity
@Table(name="riddles")
@ConditionalOnBean(RiddleComponent::class)
data class RiddleEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var title: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 2, label = "A képrejtvény", fileType = "image")
    @property:GenerateOverview(visible = false)
    var imageUrl: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 3, label = "Megoldás")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1)
    var solution: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 4, label = "Hint")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 2)
    var hint: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 5, label = "Pont")
    @property:GenerateOverview(columnName = "Pont", order = 2, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    var score: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false, name = "`order`")
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 6, label = "Sorrend")
    @property:GenerateOverview(columnName = "Sorrend", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    var order: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 7, label = "Kategória id-je")
    @property:GenerateOverview(columnName = "Kategória", order = 4, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    var categoryId: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false, length = 64)
    @property:GenerateInput(maxLength = 64, order = 8, label = "Riddle készítője", note = "Akkor jelenik meg ha nem üres")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var creator: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false, length = 64)
    @property:GenerateInput(maxLength = 64, order = 9, label = "Első megoldó",
        note = "Ezt automatikusan tölti ki a rendszer")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 7)
    var firstSolver: String = "",

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT default ''")
    @property:GenerateInput(order = 10, label = "Leírás",
        note = "Akkor jelenik meg ha nem üres",
        type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false)
    var description: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Riddle",
        view = "control/riddles",
        showPermission = StaffPermissions.PERMISSION_SHOW_RIDDLES
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RiddleEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
