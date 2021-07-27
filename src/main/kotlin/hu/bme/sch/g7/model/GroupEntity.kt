package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonIgnore
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
    @property:GenerateInput(order = 3, label = "TSZ 1: Név; Facebook url; Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "Kiss Pista;fb.com/pista1234;+36 30 6969 420")
    @property:GenerateOverview(visible = false)
    var staff1: String = "",

    @Column(nullable = false)
    @property:GenerateInput(order = 4, label = "TSZ 2: Név; Facebook url; Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    var staff2: String = "",

    @Column(nullable = false)
    @property:GenerateInput(order = 5, label = "TSZ 3: Név; Facebook url; Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    var staff3: String = "",

    @Column(nullable = false)
    @property:GenerateInput(order = 6, label = "TSZ 4: Név; Facebook url; Telefonszám",
            note = "Ha üres, nem jelenik meg", placeholder = "")
    @property:GenerateOverview(visible = false)
    var staff4: String = "",

    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_FILE, order = 7, label = "Tankör borítóképe")
    @property:GenerateOverview(visible = false)
    var coverImageUrl: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 8, label = "Longitude", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastLongitude: String = "0",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 32, order = 9, label = "Latitude", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastLatitude: String = "0",

    // FIXME: date input
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_NUMBER, order = 10, label = "Helyzet frissült ekkor", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastTimeLocationChanged: Long = 0,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 11, label = "Helyzetet frissítő felhasználó", note = "Helymeghatározás feature")
    @property:GenerateOverview(visible = false)
    var lastTimeUpdatedUser: String = "",

    // FIXME: add tankör listázása
    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, targetEntity = UserEntity::class, mappedBy = "id")
    var members: List<UserEntity?> = listOf()

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}