package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(FormComponent::class)
interface FormRepository : JpaRepository<FormEntity, Int>,
    EntityPageDataSource<FormEntity, Int> {

    fun findAllByOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now1: Long, now2: Long): List<FormEntity>
    fun findAllByAdvertizedTrueAndOpenTrueAndAvailableFromLessThanAndAvailableUntilGreaterThan(now1: Long, now2: Long): List<FormEntity>
    fun findAllByUrl(url: String): List<FormEntity>
    fun findAllBySelectedTrue(): List<FormEntity>

}
