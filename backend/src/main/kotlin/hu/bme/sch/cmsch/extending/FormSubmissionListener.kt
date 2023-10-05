package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.component.form.FormEntity
import hu.bme.sch.cmsch.component.form.ResponseEntity
import hu.bme.sch.cmsch.component.login.CmschUser

interface FormSubmissionListener {

    fun onFormSubmitted(user: CmschUser, form: FormEntity, response: ResponseEntity)

    fun onFormUpdated(user: CmschUser, form: FormEntity, response: ResponseEntity)

}