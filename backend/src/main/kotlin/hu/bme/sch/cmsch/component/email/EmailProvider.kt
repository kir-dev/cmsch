package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.model.UserEntity

interface EmailProvider {

    fun sendTextEmail(responsible: UserEntity?, subject: String, content: String, to: List<String>)

    fun sendHtmlEmail(responsible: UserEntity?, subject: String, content: String, to: List<String>)

}