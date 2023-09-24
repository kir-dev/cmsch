package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
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
    componentSettingService, env
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
            langNoSubmission
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", MinRoleSettingProxy.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    // -------------------------------------------------------------------------------------------------------------------

    val langGroup = SettingProxy(componentSettingService, component,
        "langGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Nyelvi beállítások",
        description = ""
    )

    val langTooEarly = SettingProxy(componentSettingService, component,
        "langTooEarly", "A jelentkezés még nem tölthető ki", type = SettingType.TEXT,
        fieldName = "'Túl korán' szöveg", description = "Akkor jelenik meg amikor a jelentkezés még nem tölthető ki"
    )

    val langTooLate = SettingProxy(componentSettingService, component,
        "langTooLate", "A jelentkezés már nem tölthető ki", type = SettingType.TEXT,
        fieldName = "'Túl késő' szöveg", description = "Akkor jelenik meg amikor a jelentkezési időszak már lejárt"
    )

    val langNotEnabled = SettingProxy(componentSettingService, component,
        "langNotEnabled", "A jelentkezés nem érhető el", type = SettingType.TEXT,
        fieldName = "'Nem elérhető' szöveg", description = "Akkor jelenik meg amikor a jelentkezés ki van kapcsolva"
    )

    val langFull = SettingProxy(componentSettingService, component,
        "langFull", "Nincs több férőhely", type = SettingType.TEXT,
        fieldName = "'Betelt' szöveg", description = "Akkor jelenik meg amikor a betelt az összes férőhely"
    )

    val langNotFound = SettingProxy(componentSettingService, component,
        "langNotFound", "Jelentkezés nem található", type = SettingType.TEXT,
        fieldName = "'Nem található' szöveg", description = "Akkor jelenik meg amikor az adott url-el nincs jelenetkezés " +
                "vagy a felhasználónak nincs rá joga megtekinteni."
    )

    val langSubmitted = SettingProxy(componentSettingService, component,
        "langSubmitted", "Jelentkezés beadva", type = SettingType.TEXT,
        fieldName = "'Beadva' szöveg", description = "Akkor jelenik meg amikor jelentkezés be lett adva, de még se elfogadva, se elutasítva nem lett."
    )

    val langRejected = SettingProxy(componentSettingService, component,
        "langRejected", "Jelentkezés el lett utasítva", type = SettingType.TEXT,
        fieldName = "'Elutasítva' szöveg", description = "Akkor jelenik meg amikor jelentkezés kézzel el lett utasítva."
    )

    val langAccepted = SettingProxy(componentSettingService, component,
        "langAccepted", "Jelentkezés el lett fogadva (fizetés sikeres)", type = SettingType.TEXT,
        fieldName = "'Elfogadva' szöveg", description = "Akkor jelenik meg amikor jelentkezés el lett fogadva."
    )

    val langGroupInsufficient = SettingProxy(componentSettingService, component,
        "langGroupInsufficient", "Nincs megfelelő csoportja", type = SettingType.TEXT,
        fieldName = "'Csoport nem jó' szöveg", description = "Akkor jelenik meg amikor a felhasználónak nincs megfelelő csoportja."
    )

    val langNoSubmission = SettingProxy(componentSettingService, component,
        "langNoSubmission", "Nincs leadott jelentkezés", type = SettingType.TEXT,
        fieldName = "'Nincs leadott jelentkezés' szöveg", description = "Akkor jelenik meg amikor kitölthető a jelentkezés."
    )

}
