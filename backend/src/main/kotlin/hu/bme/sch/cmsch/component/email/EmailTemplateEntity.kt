package hu.bme.sch.cmsch.component.email

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.admin.*
import hu.bme.sch.cmsch.component.EntityConfig
import hu.bme.sch.cmsch.component.event.EventEntity
import hu.bme.sch.cmsch.dto.Edit
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.model.ManagedEntity
import hu.bme.sch.cmsch.service.StaffPermissions
import jakarta.persistence.*
import org.hibernate.Hibernate
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.core.env.Environment

enum class EmailMode {
    TEXT,
    HTML
}

@Entity
@Table(name="emailTemplates")
@ConditionalOnBean(EmailComponent::class)
class EmailTemplateEntity(
    @Id
    @GeneratedValue
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(type = INPUT_TYPE_HIDDEN, visible = true, ignore = true)
    @property:GenerateOverview(renderer = OVERVIEW_TYPE_ID, columnName = "ID", order = -1)
    override var id: Int = 0,

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 64, order = 1, label = "Hivatkozási név",
        note = "Ez alapján lehet majd hivatkozni rá")
    @property:GenerateOverview(visible = true, order = 1, useForSearch = true, columnName = "Név")
    @property:ImportFormat
    var selector: String = "",

    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(maxLength = 128, order = 2, label = "Tárgy",
        note = "Ez a tárgya az emailnek")
    @property:GenerateOverview(visible = true, order = 2, useForSearch = true, columnName = "Tárgy")
    @property:ImportFormat
    var subject: String = "",

    @Lob
    @field:JsonView(value = [ Edit::class ])
    @Column(nullable = false)
    @property:GenerateInput(order = 3, label = "Tartalom",
        note = "Ez az email tartalma, a változókat {{variable}} formátumban kell feltüntetni", type = INPUT_TYPE_BLOCK_TEXT)
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var template: String = "",

    @field:JsonView(value = [ Edit::class, FullDetails::class ])
    @Enumerated(EnumType.STRING)
    @property:GenerateInput(type = INPUT_TYPE_BLOCK_SELECT, order = 4, label = "Formátum", source = [ "TEXT", "HTML" ], defaultValue = "TEXT")
    @property:GenerateOverview(visible = false)
    @property:ImportFormat
    var mode: EmailMode = EmailMode.TEXT
): ManagedEntity {

    override fun getEntityConfig(env: Environment) = EntityConfig(
        name = "EmailTemplate",
        view = "control/email-templates",
        showPermission = StaffPermissions.PERMISSION_SHOW_EVENTS
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as EmailTemplateEntity

        return id != 0 && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, selector = $selector )"
    }
}