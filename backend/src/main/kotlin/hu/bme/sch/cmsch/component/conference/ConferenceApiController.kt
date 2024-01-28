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
                sectionTitle = conferenceComponent.previousConferencesSectionTitle.getValue(),
                conferences = confEntities.previousConferences,
            ),
            registration = Registration(
                buttonText = conferenceComponent.registrationButtonText.getValue(),
                cooltixEventId = conferenceComponent.registrationCooltixEventId.getValue(),
            ),
            mobilApp = MobileApp(
                description = conferenceComponent.mobileAppDescription.getValue(),
                androidUrl = conferenceComponent.mobileAppAndroidUrl.getValue(),
                iosUrl = conferenceComponent.mobileAppIosUrl.getValue(),
            ),
            giveaway = Giveaway(
                sectionTitle = conferenceComponent.giveawaySectionTitle.getValue(),
                description = conferenceComponent.giveawayDescription.getValue(),
                pictureUrl = conferenceComponent.giveawayPictureUrl.getValue(),
                rules = conferenceComponent.giveawayRules.getValue(),
            ),
            promoVideo = PromoVideo(
                sectionTitle = conferenceComponent.promoVideoSectionTitle.getValue(),
                youtubeUrl = conferenceComponent.promoVideoYoutubeUrl.getValue(),
                description = conferenceComponent.promoVideoDescription.getValue(),
            ),
            sponsors = Sponsors(
                sectionTitle = conferenceComponent.sponsorsSectionTitle.getValue(),
                companies = confEntities.sponsors,
            ),
            organisers = confEntities.organisers,
            featuredPresentation = FeaturedPresentation(
                sectionTitle = conferenceComponent.featuredPresentationSectionTitle.getValue(),
                description = conferenceComponent.featuredPresentationDescription.getValue(),
                presentation = confEntities.featuredPresentation,
            ),
            presentations = confEntities.presentations
        )
    }

}