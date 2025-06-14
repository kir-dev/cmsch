package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["form"],
    havingValue = "true",
    matchIfMissing = false
)
class FormComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "form",
    "/form",
    "Űrlapok",
    ControlPermissions.PERMISSION_CONTROL_FORM,
    listOf(FormEntity::class, ResponseEntity::class),
    env
) {


    final override val allSettings by lazy {
        listOf(
            minRole,

            langGroup,
            langTooEarly,
            langTooLate,
            langNotEnabled,
            langFull,
            langNotFound,
            langSubmitted,
            langRejected,
            langAccepted,
            langGroupInsufficient,
            langNoSubmission,
            langMessageFromOrganizers,
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    // -------------------------------------------------------------------------------------------------------------------

    val langGroup = SettingGroup(component, "langGroup", fieldName = "Nyelvi beállítások")

    val langTooEarly = StringSettingRef(componentSettingService, component,
        "langTooEarly", "A jelentkezés még nem tölthető ki",
        fieldName = "'Túl korán' szöveg", description = "Akkor jelenik meg amikor a jelentkezés még nem tölthető ki"
    )

    val langTooLate = StringSettingRef(componentSettingService, component,
        "langTooLate", "A jelentkezés már nem tölthető ki",
        fieldName = "'Túl késő' szöveg", description = "Akkor jelenik meg amikor a jelentkezési időszak már lejárt"
    )

    val langNotEnabled = StringSettingRef(componentSettingService, component,
        "langNotEnabled", "A jelentkezés nem érhető el",
        fieldName = "'Nem elérhető' szöveg", description = "Akkor jelenik meg amikor a jelentkezés ki van kapcsolva"
    )

    val langFull = StringSettingRef(componentSettingService, component,
        "langFull", "Nincs több férőhely",
        fieldName = "'Betelt' szöveg", description = "Akkor jelenik meg amikor a betelt az összes férőhely"
    )

    val langNotFound = StringSettingRef(componentSettingService,
        component, "langNotFound", "Jelentkezés nem található", fieldName = "'Nem található' szöveg",
        description = "Akkor jelenik meg amikor az adott url-el nincs jelenetkezés vagy a felhasználónak nincs rá joga megtekinteni."
    )

    val langSubmitted = StringSettingRef(componentSettingService,
        component, "langSubmitted", "Jelentkezés beadva", fieldName = "'Beadva' szöveg",
        description = "Akkor jelenik meg amikor jelentkezés be lett adva, de még se elfogadva, se elutasítva nem lett."
    )

    val langRejected = StringSettingRef(componentSettingService, component,
        "langRejected", "Jelentkezés el lett utasítva",
        fieldName = "'Elutasítva' szöveg", description = "Akkor jelenik meg amikor jelentkezés kézzel el lett utasítva."
    )

    val langAccepted = StringSettingRef(componentSettingService, component,
        "langAccepted", "Jelentkezés el lett fogadva (fizetés sikeres)",
        fieldName = "'Elfogadva' szöveg", description = "Akkor jelenik meg amikor jelentkezés el lett fogadva."
    )

    val langGroupInsufficient = StringSettingRef(componentSettingService,
        component,
        "langGroupInsufficient",
        "Nincs megfelelő csoportja",
        fieldName = "'Csoport nem jó' szöveg",
        description = "Akkor jelenik meg amikor a felhasználónak nincs megfelelő csoportja."
    )

    val langNoSubmission = StringSettingRef(componentSettingService,
        component,
        "langNoSubmission",
        "Nincs leadott jelentkezés",
        fieldName = "'Nincs leadott jelentkezés' szöveg",
        description = "Akkor jelenik meg amikor kitölthető a jelentkezés."
    )

    val langMessageFromOrganizers = StringSettingRef(componentSettingService,
        component,
        "langMessageFromOrganizers",
        "**Üzenet a rendezőktől:**",
        fieldName = "Visszadobás üzenet fejléce",
        description = "Ha egy rendező visszadobja a profilt, akkor jelenik meg."
    )

}
