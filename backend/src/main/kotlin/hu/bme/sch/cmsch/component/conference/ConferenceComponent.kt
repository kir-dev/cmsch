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

    final override val allSettings by lazy {
        listOf(
            conferenceGroup,
            minRole,

            previousConferencesGroup,
            previousConferencesSectionTitle,

            registrationGroup,
            registrationButtonText,
            registrationCooltixEventId,

            mobileAppGroup,
            mobileAppDescription,
            mobileAppAndroidUrl,
            mobileAppIosUrl,

            giveawayGroup,
            giveawaySectionTitle,
            giveawayDescription,
            giveawayPictureUrl,
            giveawayRules,

            promoVideoGroup,
            promoVideoSectionTitle,
            promoVideoYoutubeUrl,
            promoVideoDescription,

            sponsorsGroup,
            sponsorsSectionTitle,

            featuredPresentationGroup,
            featuredPresentationSectionTitle,
            featuredPresentationDescription,
            featuredPresentationSelector,
        )
    }

    val conferenceGroup = SettingGroup(component, "conferenceGroup", fieldName = "Konferencia")

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val previousConferencesGroup = SettingGroup(component, "previousConferencesGroup",
        fieldName = "Korábbi konferenciák")

    val previousConferencesSectionTitle = StringSettingRef(componentSettingService, component,
        "previousConferencesSectionTitle", "Korábbi Konferenciák", fieldName = "previousConferences.sectionTitle mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val registrationGroup = SettingGroup(component, "registrationGroup", fieldName = "Regisztráció")

    val registrationButtonText = StringSettingRef(componentSettingService, component,
        "registrationButtonText", "Regisztráció", fieldName = "registration.buttonText mező"
    )

    val registrationCooltixEventId = StringSettingRef(componentSettingService, component,
        "registrationCooltixEventId", "https://url.com/", type = SettingType.URL,
        fieldName = "registration.cooltixEventId mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val mobileAppGroup = SettingGroup(component, "mobileAppGroup", fieldName = "Mobil App")

    val mobileAppDescription = StringSettingRef(componentSettingService, component,
        "mobileAppDescription", "Regisztráció", fieldName = "mobileApp.description mező"
    )

    val mobileAppAndroidUrl = StringSettingRef(componentSettingService, component,
        "mobileAppAndroidUrl", "https://", type = SettingType.URL, fieldName = "mobileApp.androidUrl mező"
    )

    val mobileAppIosUrl = StringSettingRef(componentSettingService, component,
        "mobileAppIosUrl", "https://", type = SettingType.URL, fieldName = "mobileApp.iosUrl mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val giveawayGroup = SettingGroup(component, "giveawayGroup", fieldName = "Nyereményjáték")

    val giveawaySectionTitle = StringSettingRef(componentSettingService, component,
        "giveawaySectionTitle", "Nyereményjáték", fieldName = "giveaway.sectionTitle mező"
    )

    val giveawayDescription = StringSettingRef(componentSettingService, component,
        "giveawayDescription", "Nyereményjáték leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "giveaway.description mező"
    )

    val giveawayPictureUrl = StringSettingRef(componentSettingService, component,
        "giveawayPictureUrl", "https://", type = SettingType.URL, fieldName = "giveaway.pictureUrl mező"
    )

    val giveawayRules = StringSettingRef(componentSettingService, component,
        "giveawayRules", "", type = SettingType.LONG_TEXT_MARKDOWN, fieldName = "giveaway.rules mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val promoVideoGroup = SettingGroup(component, "promoVideoGroup", fieldName = "Promó videó")

    val promoVideoSectionTitle = StringSettingRef(componentSettingService, component,
        "promoVideoSectionTitle", "Promóciós Videó", fieldName = "promoVideo.sectionTitle mező"
    )

    val promoVideoYoutubeUrl = StringSettingRef(componentSettingService, component,
        "promoVideoYoutubeUrl", "https://www.youtube.com/embed/xxxxxx", type = SettingType.URL,
        fieldName = "promoVideo.youtubeUrl mező", description = "Beágyazható URL-nek kell lennie." +
                " A legjobb ha a Youtubeos megosztandó iframe kódjából másolod ki!"
    )

    val promoVideoDescription = StringSettingRef(componentSettingService, component,
        "promoVideoDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "promoVideo.description mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorsGroup = SettingGroup(component, "sponsorsGroup", fieldName = "Támogatók")

    val sponsorsSectionTitle = StringSettingRef(componentSettingService, component,
        "sponsorsSectionTitle", "Támogatók", fieldName = "sponsors.sectionTitle mező"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val featuredPresentationGroup = SettingGroup(component, "featuredPresentationGroup", fieldName = "Kiemelt előadás")

    val featuredPresentationSectionTitle = StringSettingRef(componentSettingService, component,
        "featuredPresentationSectionTitle", "Promóciós Videó", fieldName = "featuredPresentation.sectionTitle mező"
    )

    val featuredPresentationDescription = StringSettingRef(componentSettingService, component,
        "featuredPresentationDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "featuredPresentation.description mező"
    )

    val featuredPresentationSelector = StringSettingRef(componentSettingService, component,
        "featuredPresentationSelector", "presentation-1", fieldName = "Kiemelt előadás selectorja"
    )

}
