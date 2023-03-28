package hu.bme.sch.cmsch.component.form

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(FormComponent::class)
interface FormRepository : JpaRepository<FormEntity, Int> {

    fun findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now1: Long, now2: Long): List<FormEntity>

    fun findAllByUrl(url: String): List<FormEntity>
    fun findAllBySelectedTrue(): List<FormEntity>

}
