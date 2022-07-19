package hu.bme.sch.cmsch.component.signup

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(SignupComponent::class)
interface SignupResponseRepository : JpaRepository<SignupResponseEntity, Int> {

    fun findAllByFormId(formId: Int): List<SignupResponseEntity>

    fun findByFormIdAndSubmitterUserId(formId: Int, submitterUserId: Int): Optional<SignupResponseEntity>

    fun countAllByFormIdAndRejectedFalse(formId: Int): Long

}
