package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.component.form.FormEntity
import hu.bme.sch.cmsch.component.form.ResponseEntity
import hu.bme.sch.cmsch.model.UserEntity

interface FormSubmissionListener {

    fun onFormSubmitted(user: UserEntity, form: FormEntity, response: ResponseEntity)

    fun onFormUpdated(user: UserEntity, form: FormEntity, response: ResponseEntity)

}