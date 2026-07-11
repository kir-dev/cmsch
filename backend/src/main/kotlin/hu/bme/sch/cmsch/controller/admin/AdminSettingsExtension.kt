package hu.bme.sch.cmsch.controller.admin

import hu.bme.sch.cmsch.component.login.CmschUser

interface AdminSettingsExtension {
    fun isAvailableFor(user: CmschUser): Boolean
    fun getCardTitle(): String
    fun getCardDescription(): String
    fun getCardFormHtml(user: CmschUser): String
}
