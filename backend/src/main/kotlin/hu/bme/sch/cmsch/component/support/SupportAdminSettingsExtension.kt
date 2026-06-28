package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.controller.admin.AdminSettingsExtension
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component

@Component
@ConditionalOnBean(SupportComponent::class)
class SupportAdminSettingsExtension(
    private val userRepository: UserRepository,
    private val userService: UserService
) : AdminSettingsExtension {

    override fun isAvailableFor(user: CmschUser): Boolean = true

    override fun getCardTitle(): String = "Ügyfélszolgálat – alapértelmezett nevem"

    override fun getCardDescription(): String =
        "Ez a név jelenik meg az ügyfelek számára, ha ügyfélszolgálaton válaszolsz. Ha üres, a rendszer a bejelentkezési nevedet használja."

    override fun getCardFormHtml(user: CmschUser): String {
        val userEntity = userRepository.findByInternalId(user.internalId)
        val currentName = userEntity
            .map { userService.resolveConfig(it.config).supportDefaultName }
            .orElse("")
        return """
            <form action="/admin/control/support/settings/default-name" method="post">
                <div class="field-group">
                    <label>Megjelenített neved</label>
                    <input type="text" name="supportDefaultName" value="${escapeHtml(currentName)}" placeholder="${userEntity.get().fullName}" maxlength="255">
                </div>
                <div class="button-group">
                    <button type="submit" class="btn btn-primary">
                        <span class="material-symbols-outlined">save</span>
                        <ins>Mentés</ins>
                    </button>
                </div>
            </form>
        """.trimIndent()
    }

    private fun escapeHtml(s: String): String = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}
