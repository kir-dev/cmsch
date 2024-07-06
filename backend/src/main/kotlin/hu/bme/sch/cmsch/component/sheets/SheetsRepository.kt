package hu.bme.sch.cmsch.component.sheets

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(SheetsComponent::class)
interface SheetsRepository : CrudRepository<SheetsEntity, Int>, EntityPageDataSource<SheetsEntity, Int> {

    fun findAllByFormTriggerAndEnabledTrue(formTrigger: Int): List<SheetsEntity>

}