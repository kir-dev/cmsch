package hu.bme.sch.cmsch.component.script

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.InputType
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.news.NewsEntity
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
@Table(name="scripts")
@ConditionalOnBean(ScriptComponent::class)
data class ScriptEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Script neve", note = "Kereshetőség miatt érdemes megadni")
    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 2, type = InputType.BLOCK_TEXT, label = "Leírás", note = "Mit csinál a script? Dokumentációs jelleggel érdemes megadni.")
    @property:GenerateOverview(visible = false)
    var description: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 3, label = "Entitások", defaultValue = "*", note = "Ezeknek az adatbázis az entitásai lesznek elérhetőek, vagy * ha minden")
    @property:GenerateOverview(visible = false)
    var entities: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, type = InputType.SWITCH, label = "Csak olvasás", note = "Read-only mód", defaultValue = "true")
    @property:GenerateOverview(visible = false)
    var readOnly: Boolean = false,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 5, type = InputType.KOTLIN_EDITOR, label = "Script", defaultValue = "context.info(\"Hello Schönherz!\")")
    @property:GenerateOverview(visible = false)
    var script: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Script",
        view = "control/scripts",
        showPermission = StaffPermissions.PERMISSION_SHOW_SCRIPTS
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
        return "id = $id, name = $name"
    }

}
