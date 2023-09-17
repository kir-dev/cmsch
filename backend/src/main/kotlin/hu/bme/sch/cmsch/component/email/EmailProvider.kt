package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.component.login.CmschUser

interface EmailProvider {

    fun sendTextEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>)

    fun sendHtmlEmail(responsible: CmschUser?, subject: String, content: String, to: List<String>)

}