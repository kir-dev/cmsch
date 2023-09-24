package hu.bme.sch.cmsch.component.challenge

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
@Table(name="challengeSubmissions")
@ConditionalOnBean(ChallengeComponent::class)
data class ChallengeSubmissionEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 1, label = "Kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    @property:ImportFormat(ignore = false, columnId = 0)
    var category: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 1, type = IMPORT_INT)
    var userId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot")
    @property:GenerateOverview(columnName = "Felhasználó", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 2)
    var userName: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 3, type = IMPORT_INT)
    var groupId: Int? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 3, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 4)
    var groupName: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 4, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5, type = IMPORT_INT)
    var score: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_TEXT, order = 5, label = "Cimke")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat(ignore = false, columnId = 6)
    var tag: String = "",

) : ManagedEntity {

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

}
