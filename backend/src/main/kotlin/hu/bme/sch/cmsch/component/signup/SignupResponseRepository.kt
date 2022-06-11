package hu.bme.sch.cmsch.component.signup

import org.springframework.data.jpa.repository.JpaRepository

interface SignupResponseRepository : JpaRepository<SignupResponseEntity, Int> {

    fun findAllByFormId(formId: Int): List<SignupResponseEntity>

}
