package hu.bme.sch.cmsch.component.support

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class SupportThreadStatus {
    WAITING_FOR_ADMIN,
    WAITING_FOR_CUSTOMER,
    DONE
}

@Entity
@Table(name = "supportThreads", indexes = [
    Index(name = "idx_supportthread_uuid", columnList = "uuid", unique = true),
    Index(name = "idx_supportthread_useremail", columnList = "userEmail"),
    Index(name = "idx_supportthread_status", columnList = "status")
])
@ConditionalOnBean(SupportComponent::class)
data class SupportThreadEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 36)
    @property:GenerateInput(maxLength = 36, order = 1, label = "UUID", enabled = false)
    @property:GenerateOverview(visible = false)
    var uuid: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 255, order = 2, label = "Tárgy")
    @property:GenerateOverview(columnName = "Tárgy", order = 1)
    var title: String = "",

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false)
    @field:JsonView(value = [Edit::class, Preview::class])
    @property:GenerateInput(type = InputType.BLOCK_SELECT, order = 3, label = "Státusz",
        source = ["WAITING_FOR_ADMIN", "WAITING_FOR_CUSTOMER", "DONE"])
    @property:GenerateOverview(visible = false)
    var status: SupportThreadStatus = SupportThreadStatus.WAITING_FOR_ADMIN,

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 128)
    @property:GenerateInput(maxLength = 128, order = 4, label = "Felelős")
    @property:GenerateOverview(columnName = "Felelős", order = 3)
    var solver: String = "",

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 5, label = "Létrehozva", enabled = false)
    @property:GenerateOverview(visible = false)
    var createdAt: Long = 0,

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 6, label = "Frissítve", enabled = false)
    @property:GenerateOverview(visible = false)
    var updatedAt: Long = 0,

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false, length = 36, unique = true)
    @property:GenerateInput(maxLength = 36, order = 7, label = "UUID titkos kulcs", enabled = false)
    @property:GenerateOverview(visible = false)
    var uuidSecret: String = "",

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false, length = 128)
    @property:GenerateInput(maxLength = 128, order = 8, label = "Felhasználó belső azonosítója", enabled = false)
    @property:GenerateOverview(visible = false)
    var userInternalId: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 9, label = "Felhasználó emailje", enabled = false)
    @property:GenerateOverview(columnName = "Email", order = 4)
    var userEmail: String = "",

    @field:JsonView(value = [Edit::class, Preview::class])
    @Column(nullable = false, length = 255)
    @property:GenerateInput(maxLength = 255, order = 10, label = "Felhasználó neve", enabled = false)
    @property:GenerateOverview(columnName = "Felhasználó", order = 5)
    var userName: String = "",

    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 11, label = "Utolsó ügyfél válasz", enabled = false)
    @property:GenerateOverview(visible = false)
    var lastCustomerAnswerAt: Long = 0,
) : ManagedEntity {

    @get:Transient
    @property:GenerateOverview(columnName = "Státusz", order = 2, renderer = OverviewType.ICON, centered = true)
    val statusIcon: String get() = when (status) {
        SupportThreadStatus.WAITING_FOR_ADMIN -> IconStatus.NEW.name
        SupportThreadStatus.WAITING_FOR_CUSTOMER -> IconStatus.PENDING.name
        SupportThreadStatus.DONE -> IconStatus.TICK.name
    }

    @get:Transient
    @property:GenerateOverview(columnName = "Várakozás", order = 6, renderer = OverviewType.TEXT, centered = true)
    val waitingDisplay: String get() {
        if (status == SupportThreadStatus.DONE) return "–"
        val referenceTime = if (lastCustomerAnswerAt > 0) lastCustomerAnswerAt else createdAt
        val elapsedSeconds = (System.currentTimeMillis() / 1000) - referenceTime
        if (elapsedSeconds < 0) return "–"
        val hours = elapsedSeconds / 3600
        val minutes = (elapsedSeconds % 3600) / 60
        return "%d:%02d".format(hours, minutes)
    }

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "SupportThread",
        view = "control/support",
        showPermission = StaffPermissions.PERMISSION_SHOW_SUPPORT_THREADS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as SupportThreadEntity
        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String = "id=$id, uuid=$uuid, title=$title"
}
