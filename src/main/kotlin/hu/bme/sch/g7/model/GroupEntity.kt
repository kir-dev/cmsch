package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="groups")
data class GroupEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var major: MajorType = MajorType.UNKNOWN,

    @Column(nullable = false)
    var coverImageUrl: String = "",

    @Column(nullable = false)
    var staffs: String = "",

    @Column(nullable = false)
    var staffMemberFacebookUrl: String = "",

    @Column(nullable = false)
    var lastLogitude: Long = 0,

    @Column(nullable = false)
    var lastLatitude: Long = 0,

    @Column(nullable = false)
    var lastTimeLocationChanged: Long = 0,

    // FIXME: connect with users

)