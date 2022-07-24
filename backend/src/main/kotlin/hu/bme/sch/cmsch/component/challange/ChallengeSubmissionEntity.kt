package hu.bme.sch.cmsch.component.challange

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import javax.persistence.*

@Entity
@Table(name="challengeSubmissions")
@ConditionalOnBean(ChallengeComponent::class)
data class ChallengeSubmissionEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(order = 2, label = "Kategória")
    @property:GenerateOverview(columnName = "Kategória", order = 2)
    @property:ImportFormat(ignore = false, columnId = 1)
    var category: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 2, type = IMPORT_INT)
    var userId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Felhasználó", entitySource = "UserEntity",
        note = "Csak akkor kell kijelölni ha felhasználók kapnak pontot")
    @property:GenerateOverview(columnName = "Felhasználó", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 3)
    var userName: String = "",

    @Column(nullable = true)
    @property:ImportFormat(ignore = false, columnId = 4, type = IMPORT_INT)
    var groupId: Int? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_ENTITY_SELECT, order = 2, label = "Csoport", entitySource = "GroupEntity",
        note = "Csak akkor kell kijelölni ha csoportok kapnak pontot")
    @property:GenerateOverview(columnName = "Csoport", order = 3, centered = true)
    @property:ImportFormat(ignore = false, columnId = 5)
    var groupName: String = "",

    @Column(nullable = false)
    @JsonView(value = [ Edit::class ])
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 9, label = "Adott pont")
    @property:GenerateOverview(columnName = "Pont", order = 5, centered = true)
    @property:ImportFormat(ignore = false, columnId = 6)
    var score: Int = 0,

) : ManagedEntity {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ChallengeSubmissionEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return "ChallengeSubmissionEntity(id=$id, category='$category', userId=$userId, userName='$userName', groupId=$groupId, groupName='$groupName', score=$score)"
    }

}