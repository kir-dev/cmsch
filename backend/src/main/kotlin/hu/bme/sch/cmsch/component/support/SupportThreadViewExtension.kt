package hu.bme.sch.cmsch.component.support

import hu.bme.sch.cmsch.component.login.CmschUser

data class SupportThreadViewButton(
    val label: String,
    val url: String,
    val icon: String = "open_in_new",
    val newPage: Boolean = true
)

interface SupportThreadViewExtension {
    fun getButtons(thread: SupportThreadEntity, user: CmschUser): List<SupportThreadViewButton>
}
