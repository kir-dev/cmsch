package hu.bme.sch.cmsch.component.support

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

@Entity
@Table(name = "supportMessages", indexes = [
    Index(name = "idx_supportmessage_threaduuid", columnList = "threadUuid"),
    Index(name = "idx_supportmessage_createdat", columnList = "createdAt")
])
@ConditionalOnBean(SupportComponent::class)
data class SupportMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 36)
    @property:GenerateInput(maxLength = 36, order = 1, label = "Thread UUID")
    @property:GenerateOverview(columnName = "Thread UUID", order = 1)
    var threadUuid: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, columnDefinition = "TEXT")
    @property:GenerateInput(type = InputType.BLOCK_TEXT, order = 2, label = "Üzenet")
    @property:GenerateOverview(visible = false)
    var content: String = "",

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 3, label = "Létrehozva", enabled = false)
    @property:GenerateOverview(columnName = "Létrehozva", order = 3)
    var createdAt: Long = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 4, label = "Küldő neve")
    @property:GenerateOverview(columnName = "Küldő", order = 4)
    var authorName: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 5, label = "Küldő email")
    @property:GenerateOverview(visible = false)
    var authorEmail: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 6, label = "Admin válasz")
    @property:GenerateOverview(columnName = "Admin", order = 5, centered = true, renderer = OverviewType.BOOLEAN)
    var fromAdmin: Boolean = false,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.SWITCH, order = 7, label = "Csak belső (nem kerül ki az ügyfélhez)")
    @property:GenerateOverview(visible = false)
    var internalOnly: Boolean = false,

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 8, label = "Valódi admin neve (belső)", enabled = false)
    @property:GenerateOverview(visible = false)
    var realAuthorName: String = "",
) : ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "SupportMessage",
        view = "control/support",
        showPermission = StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SupportMessageEntity
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = "id=$id, threadUuid=$threadUuid, fromAdmin=$fromAdmin"
}
