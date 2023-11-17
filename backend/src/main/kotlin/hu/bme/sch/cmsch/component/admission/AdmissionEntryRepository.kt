package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(AdmissionComponent::class)
interface AdmissionEntryRepository : JpaRepository<AdmissionEntryEntity, Int>,
    EntityPageDataSource<AdmissionEntryEntity, Int> {

    fun findAllByFormIdAndAllowedTrue(formId: Int): List<AdmissionEntryEntity>

    fun countAllByToken(token: String): Long

}