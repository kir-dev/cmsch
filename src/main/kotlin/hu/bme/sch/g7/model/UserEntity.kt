package hu.bme.sch.g7.model

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
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var pekId: String = "",

    @Column(nullable = false)
    var neptun: String = "",

    @Column(nullable = false)
    var fullName: String = "",

    @Column(nullable = false)
    var email: String = "",

    @Enumerated(EnumType.STRING)
    var role: RoleType = RoleType.GUEST,

    @Column(nullable = false)
    var grantSellProduct: Boolean = false,

    @Column(nullable = false)
    var grantRateAchievement: Boolean = false,

    @Column(nullable = false)
    var grantCreateAchievement: Boolean = false,

    @Column(nullable = false)
    var grantManageUsers: Boolean = false,

    //FIXME: connect
    @Column(nullable = false)
    var group: String = "",

    @Enumerated(EnumType.STRING)
    var guild: GuildType = GuildType.UNKNOWN,

    @Enumerated(EnumType.STRING)
    var major: MajorType = MajorType.UNKNOWN,
)