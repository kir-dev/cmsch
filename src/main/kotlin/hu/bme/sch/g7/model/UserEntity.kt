package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

enum class RoleType {
    GUEST,    // anyone without login
    BASIC,    // has auth.sch but not member of SSSL
    STAFF,    // member of the SSSL
    ADMIN,    // the organizers of the event
    SUPERUSER // advanced user management (able to grant admin access)
}

enum class GuildType {
    UNKNOWN,
    BLACK,
    BLUE,
    RED,
    WHITE,
    YELLOW
}

enum class MajorType {
    UNKNOWN,
    IT,
    EE,
    BPROF
}

@Entity
@Table(name="users")
data class UserEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var id: Int = 0,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var pekId: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var neptun: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    var fullName: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var email: String = "",

    @JsonView(value = [ Edit::class ])
    @Enumerated(EnumType.STRING)
    var role: RoleType = RoleType.GUEST,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var grantSellProduct: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var grantRateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var grantCreateAchievement: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var grantManageUsers: Boolean = false,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    var groupName: String = "",

    @JsonView(value = [ Edit::class ])
    @ManyToOne(targetEntity = GroupEntity::class, fetch = FetchType.LAZY)
    var group: GroupEntity? = null,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Enumerated(EnumType.STRING)
    var guild: GuildType = GuildType.UNKNOWN,

    @JsonView(value = [ Edit::class, Preview::class ])
    @Enumerated(EnumType.STRING)
    var major: MajorType = MajorType.UNKNOWN,
)