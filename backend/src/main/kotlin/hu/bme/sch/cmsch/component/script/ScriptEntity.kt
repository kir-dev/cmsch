package hu.bme.sch.cmsch.component.script

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.InputType
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.form.FormComponent
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="scripts")
@ConditionalOnBean(FormComponent::class)
class ScriptEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Script neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    var name: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 2, type = InputType.BLOCK_TEXT, label = "Script")
    @property:GenerateOverview(visible = false)
    var description: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 3, label = "Komponensek", defaultValue = "", note = "Ezeknek a komponenseknek az entitásai lesznek elérhetőek")
    @property:GenerateOverview(visible = false)
    var components: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 4, type = InputType.SWITCH, label = "Csak olvasás", note = "Read-only mód, jelenleg csak ez támogatott")
    @property:GenerateOverview(visible = false)
    var readOnly: Boolean = false,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 5, type = InputType.BLOCK_TEXT, label = "Script")
    @property:GenerateOverview(visible = false)
    var script: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Script",
        view = "control/scripts",
        showPermission = StaffPermissions.PERMISSION_SHOW_SCRIPTS
    )
}