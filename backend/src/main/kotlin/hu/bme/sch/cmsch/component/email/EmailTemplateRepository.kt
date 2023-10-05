package hu.bme.sch.cmsch.component.email

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(EmailComponent::class)
interface EmailTemplateRepository : CrudRepository<EmailTemplateEntity, Int>, EntityPageDataSource<EmailTemplateEntity, Int> {

    fun findTop1BySelector(selector: String): List<EmailTemplateEntity>

}