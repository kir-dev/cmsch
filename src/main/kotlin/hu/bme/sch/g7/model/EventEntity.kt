package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="events")
data class EventEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var url: String = "",

    @Column(nullable = false)
    var title: String = "",

    @Column(nullable = false)
    var heldDay: String = "",

    @Column(nullable = false)
    var heldInterval: String = "",

    @Column(nullable = false)
    var place: String = "",

    @Column(nullable = false)
    var previewDescription: String = "",

    @Column(nullable = false)
    var fullDescription: String = "",

    @Column(nullable = false)
    var previewImageUrl: String = "",

    @Column(nullable = false)
    var fullImageUrl: String = "",

    @Column(nullable = false)
    var ogTitle: String = "",

    @Column(nullable = false)
    var ogImage: String = "",

    @Column(nullable = false)
    var ogDescription: String = "",

    @Column(nullable = false)
    var visible: Boolean = false,
)