package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import javax.persistence.*

@Entity
@Table(name="riddleMappings")
data class RiddleMappingEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    var id: Int = 0,

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
    var completedAt: Long = 0

)
