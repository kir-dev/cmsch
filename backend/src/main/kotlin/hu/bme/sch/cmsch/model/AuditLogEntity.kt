package hu.bme.sch.cmsch.model

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.service.ControlPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.core.env.Environment

@Entity
@Table(name = "audit_logs", indexes = [Index(name = "idx_timestamp", columnList = "timestamp")])
data class AuditLogEntity(
    @Id
    @GeneratedValue
    @Column(nullable = false)
    @field:JsonView(value = [Edit::class])
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 1, label = "Szint")
    @property:GenerateOverview(columnName = "Szint", order = 1)
    @property:ImportFormat
    var level: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 2, label = "Végrehajtó")
    @property:GenerateOverview(columnName = "Végrehajtó", order = 2)
    @property:ImportFormat
    var actor: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 3, label = "Komponens")
    @property:GenerateOverview(columnName = "Komponens", order = 3)
    @property:ImportFormat
    var component: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 4, label = "Tartalom", type = InputType.BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var content: String = "",

    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, FullDetails::class, Preview::class])
    @property:GenerateInput(order = 5, label = "Időbélyeg", type = InputType.DATE)
    @property:GenerateOverview(columnName = "Időbélyeg", order = 3, renderer = OverviewType.DATE, centered = true)
    @property:ImportFormat
    var timestamp: Long = 0L,
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "Audit Log Entry",
        view = "control/audit-log-entries",
        showPermission = ControlPermissions.PERMISSION_SHOW_AUDIT_LOG
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as AuditLogEntity

        return id == other.id
    }

    override fun hashCode(): Int = Hibernate.getClass(this).hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, level = $level, actor = $actor, component = $component, content = $content, timestamp = $timestamp)"
    }
}
