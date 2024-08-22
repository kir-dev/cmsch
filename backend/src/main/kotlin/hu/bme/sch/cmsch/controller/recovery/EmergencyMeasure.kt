package hu.bme.sch.cmsch.controller.recovery

import hu.bme.sch.cmsch.component.form.FormElement
import hu.bme.sch.cmsch.component.login.CmschUser

interface EmergencyMeasure {

    val displayName: String
    val description: String
    val order: Int

    fun getFields(user: CmschUser): List<FormElement>

    fun executeMeasure(user: CmschUser, index: Int, params: Map<String, String>): String

}