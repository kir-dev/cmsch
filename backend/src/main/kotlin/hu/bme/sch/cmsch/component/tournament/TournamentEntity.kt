package hu.bme.sch.cmsch.component.tournament

import com.fasterxml.jackson.annotation.JsonView
import com.google.j2objc.annotations.GenerateObjectiveCGenerics
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="tournament")
@ConditionalOnBean(TournamentComponent::class)
data class TournamentEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.NUMBER, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 1, label = "Verseny neve")
    @property:GenerateOverview(columnName = "Név", order = 1)
    @property:ImportFormat
    var title: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 3, label = "Verseny leírása")
    @property:GenerateOverview(columnName = "Leírás", order = 2)
    @property:ImportFormat
    var description: String = "",

    @Column(nullable = true)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(maxLength = 64, order = 4, label = "Verseny helyszíne")
    @property:GenerateOverview(columnName = "Helyszín", order = 3)
    @property:ImportFormat
    var location: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.SWITCH, order = 5, label = "Lehet-e jelentkezni")
    @property:GenerateOverview(columnName = "Joinable", order = 4)
    @property:ImportFormat
    var joinable: Boolean = false,

    @Column(nullable = false)
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(columnName = "Résztvevők száma", order = 5)
    @property:ImportFormat
    var participantCount: Int = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [ FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var participants: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [ Edit::class, Preview::class, FullDetails::class ])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var status: Int = 0,

): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Tournament",
        view = "control/tournament",
        showPermission = StaffPermissions.PERMISSION_SHOW_TOURNAMENTS,
    )
    fun getTournamentService() = TournamentService.getBean()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TournamentEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id, name = '$title')"
    }

    @PreRemove
    fun preRemove() {
        getTournamentService().deleteStagesForTournament(this)
    }
}