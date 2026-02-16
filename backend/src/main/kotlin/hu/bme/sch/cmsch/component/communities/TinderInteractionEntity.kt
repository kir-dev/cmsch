package hu.bme.sch.cmsch.component.communities

import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.OverviewType
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment


@Entity
@Table(name = "tinderInteractions")
@ConditionalOnBean(CommunitiesComponent::class)
data class TinderInteractionEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateOverview(columnName = "Közösség ID", order = 1)
    var communityId: Int = 0,

    @Column(nullable = false)
    @property:GenerateOverview(columnName = "Felhasználó ID", order = 2)
    var userId: Int = 0,

    @Column(nullable = false)
    @property:GenerateOverview(renderer = OverviewType.BOOLEAN, columnName = "Liked", order = 3)
    var liked: Boolean = true,

    ): ManagedEntity {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TinderInteractionEntity

        return id != 0 && id == other.id
    }

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TinderInteraction",
        view = "control/tinder-interaction",
        showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES
    )

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id)"
    }

}
