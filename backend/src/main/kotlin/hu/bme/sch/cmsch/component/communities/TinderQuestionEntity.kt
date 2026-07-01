package hu.bme.sch.cmsch.component.communities

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment


@Entity
@Table(name = "tinderQuestions")
@ConditionalOnBean(CommunitiesComponent::class)
data class TinderQuestionEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, FullDetails::class, Preview::class ])
    @property:GenerateInput(order = 1, label = "Kérdés", enabled = true)
    @property:GenerateOverview(columnName = "Kérdés", order = 1)
    @property:ImportFormat
    var question: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.BLOCK_TEXT, order = 2, label = "Válaszlehetőségek",
        note = "Válaszlehetőségek vesszővel elválasztva")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var answerOptions: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 18, label = "Látható")
    @property:GenerateOverview(columnName = "Látható", order = 2, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var visible: Boolean = false,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment)= EntityConfig(
        name = "Question",
        view = "control/tinder-question",
        showPermission = StaffPermissions.PERMISSION_SHOW_COMMUNITIES,
    )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TinderQuestionEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}