package hu.bme.sch.cmsch.component.conference

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(ConferenceComponent::class)
class ConferenceService(
    private val conferenceComponent: ConferenceComponent,
    private val conferenceCompanyRepository: ConferenceCompanyRepository,
    private val conferenceOrganizerRepository: ConferenceOrganizerRepository,
    private val conferencePresentationRepository: ConferencePresentationRepository,
    private val conferencePresenterRepository: ConferencePresenterRepository,
    private val conferenceRepository: ConferenceRepository,
) {

    data class ConfEntities(
        val previousConferences: List<ConferenceEntity>,
        val sponsors: List<ConferenceCompanyEntity>,
        val organisers: List<ConferenceOrganizerEntity>,
        val featuredPresentation: ConferencePresentationEntity?,
        val presentations: List<ConferencePresentationEntity>,
    )

    @Transactional(readOnly = true)
    fun fetchConferenceData(): ConfEntities {
        val previousConferences = conferenceRepository.findAll()
        val sponsors = conferenceCompanyRepository.findAllByVisibleTrue()
        val organisers = conferenceOrganizerRepository.findAllByVisibleTrue()

        val featuredPresentation = conferencePresentationRepository
            .findTop1BySelector(conferenceComponent.featuredPresentationSelector.getValue())
            .firstOrNull()
            ?.let { fetchPresentation(it) }

        val presentations = conferencePresentationRepository.findAllByVisibleTrue()
        presentations.forEach { fetchPresentation(it) }

        return ConfEntities(
            previousConferences = previousConferences,
            sponsors = sponsors,
            organisers = organisers,
            featuredPresentation = featuredPresentation,
            presentations = presentations,
        )
    }

    fun fetchPresentation(presentation: ConferencePresentationEntity): ConferencePresentationEntity {
        presentation.presenter = conferencePresenterRepository
            .findTop1BySelector(presentation.presenterSelector)
            .firstOrNull()

        presentation.presenter?.let { fetchCompany(it) }

        return presentation
    }

    fun fetchCompany(presenter: ConferencePresenterEntity): ConferencePresenterEntity {
        presenter.company = conferenceCompanyRepository
            .findTop1BySelector(presenter.companySelector)
            .firstOrNull()

        return presenter
    }

}