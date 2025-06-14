package hu.bme.sch.cmsch.component.conference

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/conference")
@ConditionalOnBean(ConferenceComponent::class)
class ConferenceApiController(
    private val conferenceComponent: ConferenceComponent,
    private val conferenceService: ConferenceService,
) {

    @GetMapping("/index")
    fun index(): IndexPageData {
        val confEntities = conferenceService.fetchConferenceData()
        return IndexPageData(
            previousConferences = PreviousConferences(
                sectionTitle = conferenceComponent.previousConferencesSectionTitle,
                conferences = confEntities.previousConferences,
            ),
            registration = Registration(
                buttonText = conferenceComponent.registrationButtonText,
                cooltixEventId = conferenceComponent.registrationCooltixEventId,
            ),
            mobilApp = MobileApp(
                description = conferenceComponent.mobileAppDescription,
                androidUrl = conferenceComponent.mobileAppAndroidUrl,
                iosUrl = conferenceComponent.mobileAppIosUrl,
            ),
            giveaway = Giveaway(
                sectionTitle = conferenceComponent.giveawaySectionTitle,
                description = conferenceComponent.giveawayDescription,
                pictureUrl = conferenceComponent.giveawayPictureUrl,
                rules = conferenceComponent.giveawayRules,
            ),
            promoVideo = PromoVideo(
                sectionTitle = conferenceComponent.promoVideoSectionTitle,
                youtubeUrl = conferenceComponent.promoVideoYoutubeUrl,
                description = conferenceComponent.promoVideoDescription,
            ),
            sponsors = Sponsors(
                sectionTitle = conferenceComponent.sponsorsSectionTitle,
                companies = confEntities.sponsors,
            ),
            organisers = confEntities.organisers,
            featuredPresentation = FeaturedPresentation(
                sectionTitle = conferenceComponent.featuredPresentationSectionTitle,
                description = conferenceComponent.featuredPresentationDescription,
                presentation = confEntities.featuredPresentation,
            ),
            presentations = confEntities.presentations
        )
    }

}
