package hu.bme.sch.cmsch.component.conference

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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

    val conferenceGroup = SettingProxy(componentSettingService, component,
        "conferenceGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Konferencia",
        description = ""
    )

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val previousConferencesGroup = SettingProxy(componentSettingService, component,
        "previousConferencesGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Korábbi konferenciák",
        description = ""
    )

    val previousConferencesSectionTitle = SettingProxy(componentSettingService, component,
        "previousConferencesSectionTitle", "Korábbi Konferenciák", type = SettingType.TEXT,
        fieldName = "previousConferences.sectionTitle mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val registrationGroup = SettingProxy(componentSettingService, component,
        "registrationGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Regisztráció",
        description = ""
    )

    val registrationButtonText = SettingProxy(componentSettingService, component,
        "registrationButtonText", "Regisztráció", type = SettingType.TEXT,
        fieldName = "registration.buttonText mező", description = ""
    )

    val registrationCooltixEventId = SettingProxy(componentSettingService, component,
        "registrationCooltixEventId", "https://url.com/", type = SettingType.TEXT,
        fieldName = "registration.cooltixEventId mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val mobileAppGroup = SettingProxy(componentSettingService, component,
        "mobileAppGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Mobil App",
        description = ""
    )

    val mobileAppDescription = SettingProxy(componentSettingService, component,
        "mobileAppDescription", "Regisztráció", type = SettingType.TEXT,
        fieldName = "mobileApp.description mező", description = ""
    )

    val mobileAppAndroidUrl = SettingProxy(componentSettingService, component,
        "mobileAppAndroidUrl", "https://", type = SettingType.TEXT,
        fieldName = "mobileApp.androidUrl mező", description = ""
    )

    val mobileAppIosUrl = SettingProxy(componentSettingService, component,
        "mobileAppIosUrl", "https://", type = SettingType.TEXT,
        fieldName = "mobileApp.iosUrl mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val giveawayGroup = SettingProxy(componentSettingService, component,
        "giveawayGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Nyereményjáték",
        description = ""
    )

    val giveawaySectionTitle = SettingProxy(componentSettingService, component,
        "giveawaySectionTitle", "Nyereményjáték", type = SettingType.TEXT,
        fieldName = "giveaway.sectionTitle mező", description = ""
    )

    val giveawayDescription = SettingProxy(componentSettingService, component,
        "giveawayDescription", "Nyereményjáték leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "giveaway.description mező", description = ""
    )

    val giveawayPictureUrl = SettingProxy(componentSettingService, component,
        "giveawayPictureUrl", "https://", type = SettingType.TEXT,
        fieldName = "giveaway.pictureUrl mező", description = ""
    )

    val giveawayRules = SettingProxy(componentSettingService, component,
        "giveawayRules", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "giveaway.rules mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val promoVideoGroup = SettingProxy(componentSettingService, component,
        "promoVideoGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Promó videó",
        description = ""
    )

    val promoVideoSectionTitle = SettingProxy(componentSettingService, component,
        "promoVideoSectionTitle", "Promóciós Videó", type = SettingType.TEXT,
        fieldName = "promoVideo.sectionTitle mező", description = ""
    )

    val promoVideoYoutubeUrl = SettingProxy(componentSettingService, component,
        "promoVideoYoutubeUrl", "https://www.youtube.com/embed/xxxxxx", type = SettingType.TEXT,
        fieldName = "promoVideo.youtubeUrl mező", description = "Beágyazható URL-nek kell lennie." +
                " A legjobb ha a Youtubeos megosztandó iframe kódjából másolod ki!"
    )

    val promoVideoDescription = SettingProxy(componentSettingService, component,
        "promoVideoDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "promoVideo.description mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorsGroup = SettingProxy(componentSettingService, component,
        "sponsorsGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Támogatók",
        description = ""
    )

    val sponsorsSectionTitle = SettingProxy(componentSettingService, component,
        "sponsorsSectionTitle", "Támogatók", type = SettingType.TEXT,
        fieldName = "sponsors.sectionTitle mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val featuredPresentationGroup = SettingProxy(componentSettingService, component,
        "featuredPresentationGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Kiemelt előadás",
        description = ""
    )

    val featuredPresentationSectionTitle = SettingProxy(componentSettingService, component,
        "featuredPresentationSectionTitle", "Promóciós Videó", type = SettingType.TEXT,
        fieldName = "featuredPresentation.sectionTitle mező", description = ""
    )

    val featuredPresentationDescription = SettingProxy(componentSettingService, component,
        "featuredPresentationDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "featuredPresentation.description mező", description = ""
    )

    val featuredPresentationSelector = SettingProxy(componentSettingService, component,
        "featuredPresentationSelector", "presentation-1", type = SettingType.TEXT,
        fieldName = "Kiemelt előadás selectorja", description = ""
    )

}