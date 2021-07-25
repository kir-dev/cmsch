package hu.bme.sch.g7.model

import javax.persistence.*

@Entity
@Table(name="soldProducts")
data class SoldProductEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @ManyToOne
    var product: ProductEntity? = null,

    @ManyToOne
    var seller: UserEntity? = null,

    @ManyToOne
    var owner: UserEntity? = null,

    @Column(nullable = false)
    var payed: Boolean = false,

    @Column(nullable = false)
    var shipped: Boolean = false,

    @Column(nullable = false)
    var log: String = ""
)