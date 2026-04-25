package hu.bme.sch.cmsch.component.boat

import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "boat_signup")
@ConditionalOnBean(BoatComponent::class)
data class SignupEntity(
    @Id
    @GeneratedValue
    override var id: Int = 0,
): ManagedEntity {
    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Jelentkezés",
        view = "boat/signup",
        showPermission = StaffPermissions.PERMISSION_SHOW_EVENTS,
    )
}
