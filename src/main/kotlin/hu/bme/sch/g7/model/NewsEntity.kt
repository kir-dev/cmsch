package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="news")
data class NewsEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var url: String = "",

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var content: String = "",

    @Column(nullable = false)
    var visible: Boolean = false,

    @Column(nullable = false)
    var timestamp: Long = 0,

    @Column(nullable = false)
    var ogTitle: String = "",

    @Column(nullable = false)
    var ogImage: String = "",

    @Column(nullable = false)
    var ogDescription: String = ""
)