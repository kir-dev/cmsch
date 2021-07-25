package hu.bme.sch.g7.model

import hu.bme.sch.g7.admin.*
import javax.persistence.*

@Entity
@Table(name="groups")
data class GroupEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Tankör neve")
    @property:GenerateOverview(columnName = "Tankör", order = 1)
    var name: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 2, label = "Típus", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
    @property:GenerateOverview(visible = false)
    var major: MajorType = MajorType.UNKNOWN,

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 5, label = "Tankör borítóképe")
    @property:GenerateOverview(visible = false)
    var coverImageUrl: String = "",

    @Lob
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_TEXT, order = 3, label = "Tankörseniorok és telefonszámaik")
    @property:GenerateOverview(visible = false)
    var staffs: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 4, label = "Egyik tankörsenior facebook url-je")
    @property:GenerateOverview(visible = false)
    var staffMemberFacebookUrl: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 6, label = "Longitude", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastLogitude: String = "0",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 7, label = "Latitude", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastLatitude: String = "0",

    // FIXME: date input
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 8, label = "Helyzet frissült ekkor", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastTimeLocationChanged: Long = 0,

    // FIXME: add tankör listázása
    @OneToMany(targetEntity = UserEntity::class, mappedBy = "id")
    var members: List<UserEntity?> = listOf()

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}