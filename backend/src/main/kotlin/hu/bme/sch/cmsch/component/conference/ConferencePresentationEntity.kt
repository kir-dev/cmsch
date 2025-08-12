package hu.bme.sch.cmsch.component.conference

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class RoomType {
    IB028,
    IB025,
    OTHERS,
}

enum class LanguageType {
    EN,
    HU;

    @JsonValue
    fun getCustomString(): String {
        return name.lowercase()
    }
}

@Entity
@Table(name="conferencePresentations")
@ConditionalOnBean(ConferenceComponent::class)
data class ConferencePresentationEntity(
    @Id
    @field:JsonIgnore
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @ColumnDefault("false")
    @property:GenerateInput(type = InputType.SWITCH, order = 10, label = "Szünet-e?",
        note = "Amennyiben előadások közötti szünetet szeretnél felvenni, kapcsold be")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var isBreak: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 1, label = "Slug")
    @property:GenerateOverview(columnName = "Slug", order = 2)
    @property:ImportFormat
    var slug: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Cím")
    @property:GenerateOverview(columnName = "Cím", order = 1)
    @property:ImportFormat
    var title: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 3, label = "Terem", source = [ "IB028", "IB025", "OTHERS" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var room: RoomType = RoomType.OTHERS,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 4, label = "Nyelv", source = [ "HU", "EN" ])
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var language: LanguageType = LanguageType.HU,

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 5, label = "Kezdet ideje")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var startTime: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 6, label = "Eddig tart")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var endTime: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 7, type = InputType.BLOCK_TEXT, label = "Leírás", enabled = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var description: String = "",

    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 8, label = "Kérdések URL")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var questionsUrl: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 4, label = "Előadó a seletora",
        note = "Annak az előadónak a selectora ami ehhez az előadáshoz van rendelve. " +
                "Ha üres vagy nem létező cég van megadva, akkor null lesz.")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var presenterSelector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 9, label = "Selector",
        note = "Ez a mező van arra használva, hogy meg tudd hivatkozni ezt az entitást. " +
                "Egyedinek kell lennie, hogy működjön!")
    @property:GenerateOverview(columnName = "Selector", order = 1)
    @property:ImportFormat
    var selector: String = "",

    @field:JsonIgnore
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 10, label = "Látható")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var visible: Boolean = false,

) : ManagedEntity, Duplicatable {

    @Transient
    var presenter: ConferencePresenterEntity? = null

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ConferencePresentation",
        view = "control/conference-presentation",
        showPermission = StaffPermissions.PERMISSION_SHOW_CONFERENCE
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ConferencePresentationEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, title = $title)"
    }

    override fun duplicate(): ConferencePresentationEntity {
        return this.copy()
    }

}
