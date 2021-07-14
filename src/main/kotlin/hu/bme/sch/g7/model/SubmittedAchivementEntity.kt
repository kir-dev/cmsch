package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="submittedAchievements")
data class SubmittedAchivementEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @ManyToOne(targetEntity = AchievementEntity::class)
    var achievement: AchievementEntity? = null,

    @Column(nullable = false)
    var groupId: Int = 0,

    @Column(nullable = false)
    var textAnswer: String = "",

    @Column(nullable = false)
    var imageUrlAnswer: String = "",

    @Column(nullable = false)
    var response: String = "",

    @Column(nullable = false)
    var approved: Boolean = false,

    @Column(nullable = false)
    var rejected: Boolean = false,

    @Column(nullable = false)
    var score: Int = 0
)