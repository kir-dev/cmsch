package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="products")
data class ProductEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var imageUrl: String = "",

    @Column(nullable = false)
    var available: Boolean = false,

    @Column(nullable = false)
    var visible: Boolean = false
)