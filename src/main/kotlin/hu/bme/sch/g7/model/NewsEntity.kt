package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="news")
data class NewsEntity(
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

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var content: String = "",

    @JsonView(value = [ Edit::class, Preview::class ])
    @Column(nullable = false)
    var brief: String = "",

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var visible: Boolean = false,

    @JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    var timestamp: Long = 0,

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogTitle: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogImage: String = "",

    @JsonView(value = [ Edit::class, FullDetails::class ])
    @Column(nullable = false)
    var ogDescription: String = ""
)