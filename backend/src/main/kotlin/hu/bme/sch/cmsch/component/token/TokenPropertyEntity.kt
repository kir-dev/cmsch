package hu.bme.sch.cmsch.component.token

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.INPUT_TYPE_HIDDEN
import hu.bme.sch.cmsch.admin.OVERVIEW_TYPE_ID
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
@Table(name="tokenProperties")
@ConditionalOnBean(TokenComponent::class)
data class TokenPropertyEntity(

    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var ownerUser: UserEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var ownerGroup: GroupEntity? = null,

    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var token: TokenEntity? = null,

    @Column(nullable = false)
    var recieved: Long = 0

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TokenProperty",
        view = "control/raw-token-properties",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOKENS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TokenPropertyEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }
}
