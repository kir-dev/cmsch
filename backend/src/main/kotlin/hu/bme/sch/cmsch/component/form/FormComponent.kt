package hu.bme.sch.cmsch.component.form

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.form"])
class FormComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "form",
    "/form",
    "Űrlapok",
    ControlPermissions.PERMISSION_CONTROL_FORM,
    listOf(FormEntity::class, ResponseEntity::class),
    env
) {

    final override val menuDisplayName = null

    final override var minRole by MinRoleSettingRef(MinRoleSettingRef.ALL_ROLES, minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal")

    // -------------------------------------------------------------------------------------------------------------------

    val langGroup by SettingGroup(fieldName = "Nyelvi beállítások")

    var langTooEarly by StringSettingRef("A jelentkezés még nem tölthető ki",
        fieldName = "'Túl korán' szöveg", description = "Akkor jelenik meg amikor a jelentkezés még nem tölthető ki")

    var langTooLate by StringSettingRef("A jelentkezés már nem tölthető ki",
        fieldName = "'Túl késő' szöveg", description = "Akkor jelenik meg amikor a jelentkezési időszak már lejárt")

    var langNotEnabled by StringSettingRef("A jelentkezés nem érhető el",
        fieldName = "'Nem elérhető' szöveg", description = "Akkor jelenik meg amikor a jelentkezés ki van kapcsolva")

    var langFull by StringSettingRef("Nincs több férőhely",
        fieldName = "'Betelt' szöveg", description = "Akkor jelenik meg amikor a betelt az összes férőhely")

    var langNotFound by StringSettingRef("Jelentkezés nem található", fieldName = "'Nem található' szöveg",
        description = "Akkor jelenik meg amikor az adott url-el nincs jelenetkezés vagy a felhasználónak nincs rá joga megtekinteni.")

    var langSubmitted by StringSettingRef("Jelentkezés beadva", fieldName = "'Beadva' szöveg",
        description = "Akkor jelenik meg amikor jelentkezés be lett adva, de még se elfogadva, se elutasítva nem lett.")

    var langRejected by StringSettingRef("Jelentkezés el lett utasítva", fieldName = "'Elutasítva' szöveg",
        description = "Akkor jelenik meg amikor jelentkezés kézzel el lett utasítva.")

    var langAccepted by StringSettingRef("Jelentkezés el lett fogadva (fizetés sikeres)",
        fieldName = "'Elfogadva' szöveg", description = "Akkor jelenik meg amikor jelentkezés el lett fogadva.")

    var langGroupInsufficient by StringSettingRef("Nincs megfelelő csoportja", fieldName = "'Csoport nem jó' szöveg",
        description = "Akkor jelenik meg amikor a felhasználónak nincs megfelelő csoportja.")

    var langNoSubmission by StringSettingRef("Nincs leadott jelentkezés",
        fieldName = "'Nincs leadott jelentkezés' szöveg",
        description = "Akkor jelenik meg amikor kitölthető a jelentkezés.")

    var langMessageFromOrganizers by StringSettingRef("**Üzenet a rendezőktől:**",
        fieldName = "Visszadobás üzenet fejléce",
        description = "Ha egy rendező visszadobja a profilt, akkor jelenik meg.")

}
