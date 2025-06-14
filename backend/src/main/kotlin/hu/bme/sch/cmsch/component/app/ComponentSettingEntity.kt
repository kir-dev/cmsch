package hu.bme.sch.cmsch.component.app

import jakarta.persistence.*
import java.io.Serializable

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

    @Column(nullable = false, name = "`value`", columnDefinition = "TEXT")
    var value: String = "",

) : Serializable
