package hu.bme.sch.cmsch.component.boat

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.admin.InputType
import hu.bme.sch.cmsch.component.EntityConfig
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
import org.springframework.core.env.Environment

@Entity
@Table(name="boat_type")
data class BoatTypeEntity (
    @Id
    @GeneratedValue
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 3, order = 1, label = "Név")
    @property:GenerateOverview(columnName = "Név", order = 1, useForSearch = true)
    @property:ImportFormat
    var name: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, defaultValue = "0", order = 2, label = "Létrehozva", enabled = false, ignore = true)
    @property:ImportFormat
    var dateCreated: Long = 0

): ManagedEntity {
    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Hajó osztály",
        view = "boat/type",
        showPermission = StaffPermissions.PERMISSION_SHOW_EVENTS,
    )
}