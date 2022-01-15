package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.GenerateInput
import hu.bme.sch.cmsch.admin.GenerateOverview
import hu.bme.sch.cmsch.admin.INPUT_TYPE_HIDDEN
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

@Entity
@Table(name="tokenProperties")
data class TokenPropertyEntity(

    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var ownerUser: UserEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var ownerGroup: GroupEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @ManyToOne(fetch = FetchType.EAGER)
    var token: TokenEntity? = null,

): ManagedEntity {
    override fun toString(): String {
        return "[$id] ${ownerUser?.fullName ?: "*"} ${ownerGroup?.name ?: "*"} -> ${token?.title ?: "-"}"
    }
}
