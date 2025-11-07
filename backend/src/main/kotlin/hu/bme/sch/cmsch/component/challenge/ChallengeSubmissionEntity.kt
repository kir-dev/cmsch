package hu.bme.sch.cmsch.component.challenge

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
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
@Table(name="challengeSubmissions")
@ConditionalOnBean(ChallengeComponent::class)
data class ChallengeSubmissionEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 1, label = "Kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    @property:ImportFormat
    var category: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat
    var userId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot")
    @property:GenerateOverview(columnName = "Felhasználó", order = 3, centered = true)
    @property:ImportFormat
    var userName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat
    var groupId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.ENTITY_SELECT, order = 3, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat
    var groupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = InputType.NUMBER, min = Integer.MIN_VALUE, order = 4, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    @property:ImportFormat
    var score: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.TEXT, order = 5, label = "Cimke")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var tag: String = "",

) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ChallengeSubmission",
        view = "control/challenge",
        showPermission = StaffPermissions.PERMISSION_SHOW_CHALLENGES
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ChallengeSubmissionEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "ChallengeSubmissionEntity(id=$id, category='$category', userId=$userId, userName='$userName', groupId=$groupId, groupName='$groupName', score=$score, tag='$tag')"
    }

    override fun duplicate(): ChallengeSubmissionEntity {
        return this.copy()
    }

}
