package hu.bme.sch.cmsch.component

import java.io.Serializable
import jakarta.persistence.*

@Entity
@Table(name = "componentSettings", indexes = [
    Index(name = "idx_componentsettingentity", columnList = "component, property", unique = true)
])
class ComponentSettingEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    var id: Int = 0,

    @Column(nullable = false)
    var component: String = "",

    @Column(nullable = false)
    var property: String = "",

    @Lob
    @Column(nullable = false, name = "`value`")
    var value: String = "",

) : Serializable
