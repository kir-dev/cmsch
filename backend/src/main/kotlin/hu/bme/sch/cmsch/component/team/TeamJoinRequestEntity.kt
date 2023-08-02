package hu.bme.sch.cmsch.component.team

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
@Table(name="teamJoinRequests")
@ConditionalOnBean(TeamComponent::class)
data class TeamJoinRequestEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Felhasználó")
    @property:GenerateOverview(columnName = "Felhasználó", order = 1)
    @property:ImportFormat(ignore = false, columnId = 0)
    var userName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 2, label = "Felhasználó ID-je")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var userId: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 3, label = "Csapat")
    @property:GenerateOverview(columnName = "Csapat", order = 2)
    @property:ImportFormat(ignore = false, columnId = 2)
    var groupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, min = 0, order = 4, label = "Csapat ID-je")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    var groupId: Int = 0,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TeamJoinRequest",
        view = "control/join-requests",
        showPermission = StaffPermissions.PERMISSION_SHOW_TEAM_JOINS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TeamJoinRequestEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, user = $userName, group = $groupName)"
    }

}
