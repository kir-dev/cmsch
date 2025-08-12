package hu.bme.sch.cmsch.component.errorlog

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.Duplicatable
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment


@Entity
@Table(
    name = "errorLog",
    uniqueConstraints = [UniqueConstraint(columnNames = ["message", "stack", "userAgent", "href", "role"])]
)
@ConditionalOnBean(ErrorLogComponent::class)
data class ErrorLogEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [Edit::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OverviewType.ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false, length = 2048)
    @property:GenerateInput(maxLength = 512, type = InputType.BLOCK_TEXT, order = 1, label = "Hiba")
    @property:GenerateOverview(columnName = "Hiba", order = 1, useForSearch = true)
    @property:ImportFormat
    var message: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false, length = 25000)
    @property:GenerateInput(maxLength = 2048, type = InputType.BLOCK_TEXT, order = 2, label = "Stacktrace")
    @property:GenerateOverview(visible = false, columnName = "Stacktrace", order = 2, useForSearch = true)
    @property:ImportFormat
    var stack: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false, length = 1024)
    @property:GenerateInput(maxLength = 512, type = InputType.BLOCK_TEXT, order = 3, label = "User Agent")
    @property:GenerateOverview(visible = false, columnName = "User Agent", order = 3, useForSearch = true)
    @property:ImportFormat
    var userAgent: String = "",

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false, length = 2048)
    @property:GenerateInput(maxLength = 512, order = 4, label = "href")
    @property:GenerateOverview(columnName = "href", order = 4, useForSearch = true)
    @property:ImportFormat
    var href: String = "",

    @field:JsonView(value = [Edit::class, FullDetails::class])
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @property:GenerateInput(
        type = InputType.BLOCK_SELECT, order = 5, label = "Jogkör",
        source = ["GUEST", "BASIC", "ATTENDEE", "PRIVILEGED", "STAFF", "ADMIN", "SUPERUSER"],
        minimumRole = RoleType.ADMIN, note = "BASIC = belépett, STAFF = rendező, ADMIN = minden jog"
    )
    @property:GenerateOverview(visible = true, columnName = "Jelentő jogköre", order = 5)
    @property:ImportFormat
    var role: RoleType = RoleType.GUEST,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.NUMBER, order = 6, label = "Ennyiszer jelentve")
    @property:GenerateOverview(
        visible = true,
        columnName = "Ennyiszer jelentve",
        order = 6,
        renderer = OverviewType.NUMBER
    )
    @property:ImportFormat
    var count: Long = 1,

    @field:JsonView(value = [Edit::class, Preview::class, FullDetails::class])
    @Column(nullable = false)
    @property:GenerateInput(type = InputType.DATE, order = 7, label = "Utoljára jelentve")
    @property:GenerateOverview(
        visible = true,
        columnName = "Utoljára jelentve",
        order = 7,
        renderer = OverviewType.DATE
    )
    @property:ImportFormat
    var lastReportedAt: Long = 0,
) : ManagedEntity, Duplicatable {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "ErrorLogEntity",
        view = "control/errorlog",
        showPermission = StaffPermissions.PERMISSION_SHOW_ERROR_LOG
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ErrorLogEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id )"
    }

    override fun duplicate(): ErrorLogEntity {
        return this.copy()
    }

}
