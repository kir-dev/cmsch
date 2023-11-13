package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(FormComponent::class)
interface ResponseRepository : JpaRepository<ResponseEntity, Int>,
    EntityPageDataSource<ResponseEntity, Int> {

    fun findAllByFormId(formId: Int): List<ResponseEntity>

    fun countByFormId(formId: Int): Long

    fun findAllByFormIdAndEmail(formId: Int, email: String): List<ResponseEntity>

    fun findByFormIdAndSubmitterUserId(formId: Int, submitterUserId: Int): Optional<ResponseEntity>

    fun findByFormIdAndSubmitterGroupId(formId: Int, submitterGroupId: Int): Optional<ResponseEntity>

    fun countAllByFormIdAndRejectedFalse(formId: Int): Long

    fun countTop1ByFormIdAndSubmitterGroupId(formId: Int, userId: Int): Int

    fun countTop1ByFormIdAndSubmitterUserId(formId: Int, userId: Int): Int

}
