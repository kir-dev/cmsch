package hu.bme.sch.cmsch.component.signup

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(SignupComponent::class)
interface SignupFormRepository : JpaRepository<SignupFormEntity, Int> {

    fun findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now1: Long, now2: Long): List<SignupFormEntity>

    fun findAllByUrl(url: String): List<SignupFormEntity>

}
