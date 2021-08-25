package hu.bme.sch.g7.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.dto.Edit
import hu.bme.sch.g7.dto.FullDetails
import hu.bme.sch.g7.dto.Preview
import javax.persistence.*

@Entity
@Table(name="groups")
data class GroupEntity(
        @Id
        @GeneratedValue
        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
        @property:GenerateOverview(visible = false)
        override var id: Int = 0,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(maxLength = 64, order = 1, label = "Tankör neve")
        @property:GenerateOverview(columnName = "Tankör", order = 1)
        @property:ImportFormat(ignore = false, columnId = 0)
        var name: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 2, label = "Típus", source = [ "UNKNOWN", "IT", "EE", "BPROF" ])
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 1)
        var major: MajorType = MajorType.UNKNOWN,

        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(order = 3, label = "TSZ 1: Név| Facebook url| Telefonszám",
                note = "Ha üres, nem jelenik meg", placeholder = "Kiss Pista | fb.com/pista1234 | +36 30 6969 420")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 3)
        var staff1: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(order = 4, label = "TSZ 2: Név| Facebook url| Telefonszám",
                note = "Ha üres, nem jelenik meg", placeholder = "")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 4)
        var staff2: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(order = 5, label = "TSZ 3: Név| Facebook url| Telefonszám",
                note = "Ha üres, nem jelenik meg", placeholder = "")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 5)
        var staff3: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class ])
        @property:GenerateInput(order = 6, label = "TSZ 4: Név| Facebook url| Telefonszám",
                note = "Ha üres, nem jelenik meg", placeholder = "")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 6)
        var staff4: String = "",

        @Column(nullable = false)
        @JsonView(value = [ Edit::class, FullDetails::class ])
        @property:GenerateInput(type = INPUT_TYPE_FILE, order = 7, label = "Tankör borítóképe")
        @property:GenerateOverview(visible = false)
        var coverImageUrl: String = "",

        @JsonIgnore
        @OneToMany(fetch = FetchType.LAZY, targetEntity = UserEntity::class, mappedBy = "group")
        @property:GenerateInput(type = INPUT_TYPE_LIST_ENTITIES, order = 12, label = "Tankör tagjai",
                ignore = true, enabled = false, entitySource = "UserEntity")
        @property:GenerateOverview(visible = false)
        var members: List<UserEntity> = listOf(),

        @JsonView(value = [ Edit::class ])
        @Column(nullable = false)
        @property:GenerateInput(type = INPUT_TYPE_SWITCH, order = 13, label = "Játszik a tankör versenyben?")
        @property:GenerateOverview(visible = false)
        @property:ImportFormat(ignore = false, columnId = 2)
        var races: Boolean = false

): ManagedEntity {
    override fun toString(): String {
        return "[$id]: $name"
    }
}