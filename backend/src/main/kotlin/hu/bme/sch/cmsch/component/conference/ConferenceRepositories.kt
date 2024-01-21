package hu.bme.sch.cmsch.component.conference

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ConferenceComponent::class)
interface ConferenceRepository : CrudRepository<ConferenceEntity, Int>, EntityPageDataSource<ConferenceEntity, Int> {
    override fun findAll(): List<ConferenceEntity>
}

@Repository
@ConditionalOnBean(ConferenceComponent::class)
interface ConferenceCompanyRepository : CrudRepository<ConferenceCompanyEntity, Int>, EntityPageDataSource<ConferenceCompanyEntity, Int> {
    fun findAllByVisibleTrue(): List<ConferenceCompanyEntity>
    fun findTop1BySelector(companySelector: String): List<ConferenceCompanyEntity>
}

@Repository
@ConditionalOnBean(ConferenceComponent::class)
interface ConferencePresenterRepository : CrudRepository<ConferencePresenterEntity, Int>, EntityPageDataSource<ConferencePresenterEntity, Int> {
    fun findTop1BySelector(presenterSelector: String): List<ConferencePresenterEntity>
}

@Repository
@ConditionalOnBean(ConferenceComponent::class)
interface ConferencePresentationRepository : CrudRepository<ConferencePresentationEntity, Int>, EntityPageDataSource<ConferencePresentationEntity, Int> {
    fun findTop1BySelector(selector: String): List<ConferencePresentationEntity>
    fun findAllByVisibleTrue(): List<ConferencePresentationEntity>
}

@Repository
@ConditionalOnBean(ConferenceComponent::class)
interface ConferenceOrganizerRepository : CrudRepository<ConferenceOrganizerEntity, Int>, EntityPageDataSource<ConferenceOrganizerEntity, Int> {
    fun findAllByVisibleTrue(): List<ConferenceOrganizerEntity>
}