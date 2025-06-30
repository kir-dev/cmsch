package hu.bme.sch.cmsch.component.conference

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["conference"],
    havingValue = "true",
    matchIfMissing = false
)
class ConferenceComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "conference",
    "/",
    "Konferencia",
    ControlPermissions.PERMISSION_CONTROL_CONFERENCE,
    listOf(
        ConferenceCompanyEntity::class,
        ConferenceEntity::class,
        ConferenceOrganizerEntity::class,
        ConferencePresentationEntity::class,
        ConferencePresenterEntity::class,
    ),
    env
) {

    val conferenceGroup by SettingGroup(fieldName = "Konferencia")

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    /// -------------------------------------------------------------------------------------------------------------------

    val previousConferencesGroup by SettingGroup(fieldName = "Korábbi konferenciák")

    var previousConferencesSectionTitle by StringSettingRef("Korábbi Konferenciák",
        fieldName = "previousConferences.sectionTitle mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val registrationGroup by SettingGroup(fieldName = "Regisztráció")

    var registrationButtonText by StringSettingRef("Regisztráció", fieldName = "registration.buttonText mező")

    var registrationCooltixEventId by StringSettingRef("https://url.com/", type = SettingType.URL,
        fieldName = "registration.cooltixEventId mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val mobileAppGroup by SettingGroup(fieldName = "Mobil App")

    var mobileAppDescription by StringSettingRef("Regisztráció", fieldName = "mobileApp.description mező")

    var mobileAppAndroidUrl by StringSettingRef("https://",
        type = SettingType.URL, fieldName = "mobileApp.androidUrl mező")

    var mobileAppIosUrl by StringSettingRef("https://", type = SettingType.URL, fieldName = "mobileApp.iosUrl mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val giveawayGroup by SettingGroup(fieldName = "Nyereményjáték")

    var giveawaySectionTitle by StringSettingRef("Nyereményjáték", fieldName = "giveaway.sectionTitle mező")

    var giveawayDescription by StringSettingRef("Nyereményjáték leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "giveaway.description mező")

    var giveawayPictureUrl by StringSettingRef("https://", type = SettingType.URL,
        fieldName = "giveaway.pictureUrl mező")

    var giveawayRules by StringSettingRef(type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "giveaway.rules mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val promoVideoGroup by SettingGroup(fieldName = "Promó videó")

    var promoVideoSectionTitle by StringSettingRef("Promóciós Videó", fieldName = "promoVideo.sectionTitle mező")

    var promoVideoYoutubeUrl by StringSettingRef("https://www.youtube.com/embed/xxxxxx", type = SettingType.URL,
        fieldName = "promoVideo.youtubeUrl mező",
        description = "Beágyazható URL-nek kell lennie. A legjobb ha a Youtubeos megosztandó iframe kódjából másolod ki!")

    var promoVideoDescription by StringSettingRef("Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "promoVideo.description mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorsGroup by SettingGroup(fieldName = "Támogatók")

    var sponsorsSectionTitle by StringSettingRef("Támogatók", fieldName = "sponsors.sectionTitle mező")

    /// -------------------------------------------------------------------------------------------------------------------

    val featuredPresentationGroup by SettingGroup(fieldName = "Kiemelt előadás")

    var featuredPresentationSectionTitle by StringSettingRef("Promóciós Videó",
        fieldName = "featuredPresentation.sectionTitle mező")

    var featuredPresentationDescription by StringSettingRef("Promóciós Videó leírása ide jön",
        type = SettingType.LONG_TEXT, fieldName = "featuredPresentation.description mező")

    var featuredPresentationSelector by StringSettingRef("presentation-1", fieldName = "Kiemelt előadás selectorja")

}
