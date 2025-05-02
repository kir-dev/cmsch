package hu.bme.sch.cmsch.component.conference

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
@ConditionalOnBean(ConferenceComponent::class)
class ConferenceSeedConfig(
    private val conferenceRepository: ConferenceRepository,
    private val conferenceCompanyRepository: ConferenceCompanyRepository,
    private val conferencePresenterRepository: ConferencePresenterRepository,
    private val conferencePresentationRepository: ConferencePresentationRepository,
    private val conferenceOrganizerRepository: ConferenceOrganizerRepository,
) {

    @PostConstruct
    fun seedData() {
        addConference()
        addCompanies()
        addPresenters()
        addPresentations()
        addOrganizers()
    }

    private fun addConference() {
        if (conferenceRepository.count() != 0L) return
        conferenceRepository.saveAll(
            listOf(
                ConferenceEntity(
                    title = "Test Conference 2022",
                    priority = 0,
                    imageUrlsArray = "https://example.com/image1.jpg,https://example.com/image2.jpg"
                ),
                ConferenceEntity(
                    title = "Test Conference 2023",
                    priority = 1,
                    imageUrlsArray = "https://example.com/image1.jpg,https://example.com/image2.jpg"
                ),
                ConferenceEntity(
                    title = "Test Conference 2024",
                    priority = 69,
                    imageUrlsArray = ""
                )
            )
        )
    }

    private fun addCompanies() {
        if (conferenceCompanyRepository.count() != 0L) return
        conferenceCompanyRepository.saveAll(
            listOf(
                ConferenceCompanyEntity(
                    name = "Test Company 1",
                    selector = "test-company-1",
                    visible = true,
                    logoUrl = "https://example.com/logo1.jpg",
                    category = SponsorCategory.MAIN_SPONSOR,
                    url = "https://example.com",
                ),
                ConferenceCompanyEntity(
                    name = "Test Company 2",
                    selector = "test-company-2",
                    visible = true,
                    logoUrl = "https://example.com/2.jpg",
                    category = SponsorCategory.FEATURED_SPONSOR,
                    url = "https://example.com",
                ),
                ConferenceCompanyEntity(
                    name = "Test Company 3",
                    selector = "test-company-3",
                    visible = true,
                    logoUrl = "https://example.com/3.jpg",
                    category = SponsorCategory.SPONSOR,
                    url = "https://example.com",
                ),
                ConferenceCompanyEntity(
                    name = "Invisible Test Company",
                    selector = "invisible-test-company",
                    visible = false,
                    logoUrl = "https://example.com/4.jpg",
                    category = SponsorCategory.SPONSOR,
                    url = "https://example.com",
                )
            )
        )
    }

    private fun addPresenters() {
        if (conferencePresenterRepository.count() != 0L) return
        conferencePresenterRepository.saveAll(
            listOf(
                ConferencePresenterEntity(
                    name = "Test Presenter 1",
                    selector = "test-presenter-1",
                    visible = true,
                    rank = "Test Rank 1",
                    companySelector = "test-company-1",
                    pictureUrl = "https://example.com/presenter1.jpg",
                ),
                ConferencePresenterEntity(
                    name = "Test Presenter 2",
                    selector = "test-presenter-2",
                    visible = true,
                    rank = "Test Rank 2",
                    companySelector = "test-company-3",
                    pictureUrl = "https://example.com/presenter2.jpg",
                ),
                ConferencePresenterEntity(
                    name = "Invisible Test Presenter",
                    selector = "test-presenter-3",
                    visible = false,
                    rank = "Test Rank 3",
                    companySelector = "test-company-2",
                    pictureUrl = "https://example.com/presenter3.jpg",
                ),
            )
        )
    }

    private fun addPresentations() {
        if (conferencePresentationRepository.count() != 0L) return
        conferencePresentationRepository.saveAll(
            listOf(
                ConferencePresentationEntity(
                    slug = "test-presentation-1",
                    title = "Test Presentation 1",
                    room = RoomType.IB028,
                    language = LanguageType.EN,
                    visible = true,
                    presenterSelector = "test-presenter-1",
                    selector = "test-presentation-1",
                    description = "Test Description 1",
                    startTime = "2021-01-01 12:00:00",
                    endTime = "2021-01-01 13:00:00",
                    questionsUrl = "https://example.com",
                ),
                ConferencePresentationEntity(
                    slug = "test-presentation-2",
                    title = "Test Presentation 2",
                    room = RoomType.OTHERS,
                    language = LanguageType.EN,
                    visible = true,
                    presenterSelector = "test-presenter-2",
                    selector = "test-presentation-2",
                    description = "Test Description 2",
                    startTime = "2021-01-01 12:00:00",
                    endTime = "2021-01-01 13:00:00",
                    questionsUrl = "https://example.com",
                ),
                ConferencePresentationEntity(
                    slug = "test-presentation-3",
                    title = "Test Presentation 3",
                    room = RoomType.IB025,
                    language = LanguageType.EN,
                    visible = true,
                    presenterSelector = "test-presenter-3",
                    selector = "test-presentation-3",
                    description = "Test Description 3",
                    startTime = "2021-01-01 12:00:00",
                    endTime = "2021-01-01 13:00:00",
                    questionsUrl = "https://example.com",
                ),
                ConferencePresentationEntity(
                    slug = "test-presentation-4",
                    title = "Test Presentation 4",
                    room = RoomType.IB028,
                    language = LanguageType.HU,
                    visible = true,
                    presenterSelector = "test-presenter-4",
                    selector = "test-presentation-4",
                    description = "Test Description 4",
                    startTime = "2021-01-01 12:00:00",
                    endTime = "2021-01-01 13:00:00",
                    questionsUrl = "https://example.com",
                ),
                ConferencePresentationEntity(
                    slug = "test-presentation-5",
                    title = "Test Presentation 5",
                    room = RoomType.IB028,
                    language = LanguageType.HU,
                    visible = false,
                    presenterSelector = "test-presenter-5",
                    selector = "test-presentation-5",
                    description = "Test Description 5",
                    startTime = "2021-01-01 12:00:00",
                    endTime = "2021-01-01 13:00:00",
                    questionsUrl = "https://example.com",
                ),
            )
        )
    }

    private fun addOrganizers() {
        if (conferenceOrganizerRepository.count() != 0L) return
        conferenceOrganizerRepository.saveAll(
            listOf(
                ConferenceOrganizerEntity(
                    name = "Test Organizer 1",
                    rank = "Test Rank 1",
                    emailAddress = " ",
                    pictureUrl = "https://example.com/organizer1.jpg",
                    priority = 0,
                    visible = true,
                ),
                ConferenceOrganizerEntity(
                    name = "Test Organizer 2",
                    rank = "Test Rank 2",
                    emailAddress = " ",
                    pictureUrl = "https://example.com/organizer2.jpg",
                    priority = 1,
                    visible = true,
                ),
                ConferenceOrganizerEntity(
                    name = "Invisible Test Organizer",
                    rank = "Test Rank 3",
                    emailAddress = " ",
                    pictureUrl = "https://example.com/organizer3.jpg",
                    priority = 2,
                    visible = false,
                ),
            )
        )
    }
}
