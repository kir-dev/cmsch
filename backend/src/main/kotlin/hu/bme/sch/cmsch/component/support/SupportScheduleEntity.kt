package hu.bme.sch.cmsch.component.support

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "supportSchedules")
@ConditionalOnBean(SupportComponent::class)
data class SupportScheduleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 128)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Felhasználó belső azonosítója",
        note = "A felhasználó belső azonosítója (internalId), aki a beosztásban szerepel")
    @property:GenerateOverview(columnName = "Azonosító", order = 1)
    var supportUserId: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 2, label = "Megjelenített név",
        note = "A megkeresésben megjelenő felelős neve")
    @property:GenerateOverview(columnName = "Név", order = 2)
    var supportUserName: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(name = "from_time", nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 3, label = "Elérhető: -tól")
    @property:GenerateOverview(columnName = "Tól", order = 3, renderer = OverviewType.DATE)
    var from: Long = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(name = "to_time", nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 4, label = "Elérhető: -ig")
    @property:GenerateOverview(columnName = "Ig", order = 4, renderer = OverviewType.DATE)
    var to: Long = 0,
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "SupportSchedule",
        view = "control/support-schedule",
        showPermission = StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SupportScheduleEntity
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = "id=$id, supportUserId=$supportUserId, supportUserName=$supportUserName, from=$from, to=$to"
}
