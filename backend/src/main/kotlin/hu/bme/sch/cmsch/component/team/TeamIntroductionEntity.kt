package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "teamIntroductions")
@ConditionalOnBean(TeamComponent::class)
data class TeamIntroductionEntity(

    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Beküldve ekkor", enabled = false, ignore = true)
    @property:GenerateOverview(visible = true, renderer = OverviewType.DATE, columnName = "Létrehozva", order = 6)
    @property:ImportFormat
    var creationDate: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var group: GroupEntity? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(order = 17, label = "Bemutatkozás", type = InputType.BLOCK_TEXT)
    @property:GenerateOverview(renderer = OverviewType.TEXT, columnName = "Bemutatkozás", order = 2)
    @property:ImportFormat
    var introduction: String = "",

    @property:GenerateInput(order = 19, label = "Logó url", enabled = true)
    @property:GenerateOverview(columnName = "Logó", renderer = OverviewType.IMAGE)
    @property:ImportFormat
    var logo: String? = null,

    @Column(nullable = false)
    @property:GenerateInput(
        type = InputType.SWITCH,
        order = 8,
        label = "Elfogadva",
        note = "Ha ez igaz az felülírja az elutasított státuszt"
    )
    @property:GenerateOverview(columnName = "Elfogadva", order = 3, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var approved: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 9, label = "Elutasítva")
    @property:GenerateOverview(columnName = "Elutasítva", order = 4, centered = true, renderer = OverviewType.BOOLEAN)
    @property:ImportFormat
    var rejected: Boolean = false,

    @Column(nullable = false)
    @property:GenerateInput(
        type = InputType.BLOCK_TEXT,
        order = 7,
        label = "Elutasítás oka",
    )
    @property:GenerateOverview(columnName = "Elutasítás oka", order = 5)
    @property:ImportFormat
    var rejectionReason: String = "",

    ) : ManagedEntity {

    @property:GenerateOverview(columnName = "Csapat", order = 0)
    val groupName get() = group?.name ?: ""

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "TeamIntroduction",
        view = "control/introductions",
        showPermission = StaffPermissions.PERMISSION_SHOW_TEAM_INTRODUCTIONS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as TeamIntroductionEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

}
