package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.news.NewsEntity
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name="scriptResults")
@ConditionalOnBean(ScriptComponent::class)
data class ScriptResultEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(order = 1, label = "Script ID", enabled = false)
    @property:GenerateOverview(order = 1, columnName = "Script ID", renderer = OverviewType.NUMBER)
    var scriptId: Int = 0,

    @Column(nullable = false)
    @property:GenerateInput(order = 2, label = "Futtató neve", enabled = false)
    @property:GenerateOverview(order = 2, columnName = "Futtató neve", useForSearch = true)
    var userName: String? = null,

    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Futtató ID-je", enabled = false)
    @property:GenerateOverview(visible = false)
    var userId: Int = 0,

    @Column(nullable = false, name = "`timestamp`")
    @property:GenerateInput(type = InputType.DATE, order = 4, label = "Dátum", min = 0, defaultValue = "0")
    @property:GenerateOverview(order = 3, columnName = "Dátum", renderer = OverviewType.DATE, centered = true)
    @property:ImportFormat
    var timestamp: Long = 0L,

    @Column(nullable = false, columnDefinition = "TEXT", name = "`result`")
    @property:GenerateInput(order = 5, type = InputType.BLOCK_TEXT, label = "Result")
    @property:GenerateOverview(visible = false)
    var result: String = "",

    @Column(nullable = false)
    @property:GenerateInput(order = 6, type = InputType.SWITCH, label = "Fut", note = "Futás sikeres", enabled = false)
    @property:GenerateOverview(order = 4, columnName = "Fut", renderer = OverviewType.BOOLEAN)
    var running: Boolean = true,

    @Column(nullable = false)
    @property:GenerateInput(order = 7, type = InputType.SWITCH, label = "Sikeres", note = "Futás sikeres", enabled = false)
    @property:GenerateOverview(order = 5, columnName = "Sikeres", renderer = OverviewType.BOOLEAN)
    var success: Boolean = false,

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @property:GenerateInput(order = 8, type = InputType.BLOCK_TEXT, label = "Artifactok", defaultValue = "[]")
    @property:GenerateOverview(visible = false)
    var artifacts: String = "",

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @property:GenerateInput(order = 9, type = InputType.BLOCK_TEXT, label = "Logok", defaultValue = "[]")
    @property:GenerateOverview(visible = false)
    var logs: String = "",

    @Column(nullable = false, name = "`duration`")
    @property:GenerateInput(type = InputType.NUMBER, order = 10, label = "Futási idő (ms)", min = 0, defaultValue = "0")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var duration: Long = 0L,

) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ScriptResult",
        view = "control/script-result",
        showPermission = StaffPermissions.PERMISSION_SHOW_SCRIPTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as NewsEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return "id = $id, script = $scriptId, timestamp = $timestamp"
    }

}