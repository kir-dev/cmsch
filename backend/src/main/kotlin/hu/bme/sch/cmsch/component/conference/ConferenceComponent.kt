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

    val conferenceGroup = ControlGroup(component, "conferenceGroup", fieldName = "Konferencia")

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val previousConferencesGroup = ControlGroup(component, "previousConferencesGroup",
        fieldName = "Korábbi konferenciák")

    val previousConferencesSectionTitle = StringSettingRef(componentSettingService, component,
        "previousConferencesSectionTitle", "Korábbi Konferenciák", type = SettingType.TEXT,
        fieldName = "previousConferences.sectionTitle mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val registrationGroup = ControlGroup(component, "registrationGroup", fieldName = "Regisztráció")

    val registrationButtonText = StringSettingRef(componentSettingService, component,
        "registrationButtonText", "Regisztráció", type = SettingType.TEXT,
        fieldName = "registration.buttonText mező", description = ""
    )

    val registrationCooltixEventId = StringSettingRef(componentSettingService, component,
        "registrationCooltixEventId", "https://url.com/", type = SettingType.URL,
        fieldName = "registration.cooltixEventId mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val mobileAppGroup = ControlGroup(component, "mobileAppGroup", fieldName = "Mobil App")

    val mobileAppDescription = StringSettingRef(componentSettingService, component,
        "mobileAppDescription", "Regisztráció", type = SettingType.TEXT,
        fieldName = "mobileApp.description mező", description = ""
    )

    val mobileAppAndroidUrl = StringSettingRef(componentSettingService, component,
        "mobileAppAndroidUrl", "https://", type = SettingType.URL,
        fieldName = "mobileApp.androidUrl mező", description = ""
    )

    val mobileAppIosUrl = StringSettingRef(componentSettingService, component,
        "mobileAppIosUrl", "https://", type = SettingType.URL,
        fieldName = "mobileApp.iosUrl mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val giveawayGroup = ControlGroup(component, "giveawayGroup", fieldName = "Nyereményjáték")

    val giveawaySectionTitle = StringSettingRef(componentSettingService, component,
        "giveawaySectionTitle", "Nyereményjáték", type = SettingType.TEXT,
        fieldName = "giveaway.sectionTitle mező", description = ""
    )

    val giveawayDescription = StringSettingRef(componentSettingService, component,
        "giveawayDescription", "Nyereményjáték leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "giveaway.description mező", description = ""
    )

    val giveawayPictureUrl = StringSettingRef(componentSettingService, component,
        "giveawayPictureUrl", "https://", type = SettingType.URL,
        fieldName = "giveaway.pictureUrl mező", description = ""
    )

    val giveawayRules = StringSettingRef(componentSettingService, component,
        "giveawayRules", "", type = SettingType.LONG_TEXT_MARKDOWN,
        fieldName = "giveaway.rules mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val promoVideoGroup = ControlGroup(component, "promoVideoGroup", fieldName = "Promó videó")

    val promoVideoSectionTitle = StringSettingRef(componentSettingService, component,
        "promoVideoSectionTitle", "Promóciós Videó", type = SettingType.TEXT,
        fieldName = "promoVideo.sectionTitle mező", description = ""
    )

    val promoVideoYoutubeUrl = StringSettingRef(componentSettingService, component,
        "promoVideoYoutubeUrl", "https://www.youtube.com/embed/xxxxxx", type = SettingType.URL,
        fieldName = "promoVideo.youtubeUrl mező", description = "Beágyazható URL-nek kell lennie." +
                " A legjobb ha a Youtubeos megosztandó iframe kódjából másolod ki!"
    )

    val promoVideoDescription = StringSettingRef(componentSettingService, component,
        "promoVideoDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "promoVideo.description mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val sponsorsGroup = ControlGroup(component, "sponsorsGroup", fieldName = "Támogatók")

    val sponsorsSectionTitle = StringSettingRef(componentSettingService, component,
        "sponsorsSectionTitle", "Támogatók", type = SettingType.TEXT,
        fieldName = "sponsors.sectionTitle mező", description = ""
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val featuredPresentationGroup = ControlGroup(component, "featuredPresentationGroup", fieldName = "Kiemelt előadás")

    val featuredPresentationSectionTitle = StringSettingRef(componentSettingService, component,
        "featuredPresentationSectionTitle", "Promóciós Videó", type = SettingType.TEXT,
        fieldName = "featuredPresentation.sectionTitle mező", description = ""
    )

    val featuredPresentationDescription = StringSettingRef(componentSettingService, component,
        "featuredPresentationDescription", "Promóciós Videó leírása ide jön", type = SettingType.LONG_TEXT,
        fieldName = "featuredPresentation.description mező", description = ""
    )

    val featuredPresentationSelector = StringSettingRef(componentSettingService, component,
        "featuredPresentationSelector", "presentation-1", type = SettingType.TEXT,
        fieldName = "Kiemelt előadás selectorja", description = ""
    )

}
