package hu.bme.sch.cmsch.component.conference

data class PreviousConferences(
    var sectionTitle: String = "",
    var conferences: List<ConferenceEntity> = listOf(),
)

data class Registration(
    var buttonText: String,
    var cooltixEventId: String,
)

data class MobileApp(
    var description: String,
    var androidUrl: String,
    var iosUrl: String,
)

data class Giveaway(
    var sectionTitle: String,
    var description: String,
    var pictureUrl: String,
    var rules: String,
)

data class PromoVideo(
    var sectionTitle: String,
    var youtubeUrl: String,
    var description: String,
)

data class Sponsors(
    var sectionTitle: String,
    var companies: List<ConferenceCompanyEntity>,
)

data class FeaturedPresentation(
    var sectionTitle: String,
    var description: String,
    var presentation: ConferencePresentationEntity?,
)

data class IndexPageData(
    var previousConferences: PreviousConferences,
    var registration: Registration,
    var mobilApp: MobileApp,
    var giveaway: Giveaway,
    var promoVideo: PromoVideo,
    var sponsors: Sponsors,
    var organisers: List<ConferenceOrganizerEntity>,
    var featuredPresentation: FeaturedPresentation,
    var presentations: List<ConferencePresentationEntity>,
)
