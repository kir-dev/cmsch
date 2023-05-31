package hu.bme.sch.cmsch.component.riddle

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment
import jakarta.persistence.*

@Entity
@Table(name="riddleMappings")
@ConditionalOnBean(RiddleComponent::class)
data class RiddleMappingEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    override var id: Int = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    var riddle: RiddleEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    var ownerUser: UserEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    var ownerGroup: GroupEntity? = null,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    var hintUsed: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    var completed: Boolean = false,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    var completedAt: Long = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    var attemptCount: Int = 0

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "RiddleMapping",
        view = if (env.getProperty("hu.bme.sch.cmsch.startup.riddle-ownership-mode") === "USER")
            "control/riddles-by-users"
        else
            "control/riddles-by-groups",
        showPermission = StaffPermissions.PERMISSION_SHOW_RIDDLE_SUBMISSIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as RiddleMappingEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
