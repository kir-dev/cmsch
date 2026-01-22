package hu.bme.sch.cmsch.component.communities

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.ImportFormat
import hu.bme.sch.cmsch.admin.InputType
import hu.bme.sch.cmsch.admin.OverviewType
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
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment

@Entity
@Table(name = "tinderAnswers")
@ConditionalOnBean(CommunitiesComponent::class)
data class TinderAnswerEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = true)
    @property:ImportFormat
    var communityId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Csoport", order = 1)
    var communityName: String = "",

    @Column(nullable = true)
    var userId: Int? = null,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateOverview(columnName = "Felhasználó", order = 2)
    var userName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 3, label = "Válaszok", enabled = true, note="Válaszok indexei vesszővel elválasztva")
    @property:GenerateOverview(columnName = "Válaszok", order = 3)
    var answers: String = "",

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Answer",
        view = "control/tinder-answer",
        showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TinderAnswerEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
