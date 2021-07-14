package hu.bme.sch.g7.model

import javax.persistence.*

enum class AchievementType {
    TEXT,
    IMAGE,
    URL
}

@Entity
@Table(name="achievements")
data class AchievementEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var expectedResultDescription: String = "",

    @Enumerated(EnumType.STRING)
    var type: AchievementType = AchievementType.TEXT,

    @Column(nullable = false)
    var availableFrom: Long = 0,

    @Column(nullable = false)
    var availableTo: Long = 0,

    @Column(nullable = false)
    var maxScore: Int = 0,

    @Column(nullable = false)
    var visible: Boolean = false,

    @Column(nullable = false)
    var order: Long = 0
)