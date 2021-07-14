package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="events")
data class EventEntity(
    @Id
    @GeneratedValue
    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var id: Int = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var url: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var title: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var heldTimestamp: Long = 0,

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var heldDay: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var heldInterval: String = "",

    @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @Column(nullable = false)
    var place: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    var previewDescription: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var fullDescription: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    var previewImageUrl: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var fullImageUrl: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogImage: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogDescription: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var visible: Boolean = false,
)