package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["pushnotification"],
    havingValue = "true",
    matchIfMissing = false
)
class PushNotificationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "pushnotification",
    "/pushnotification",
    "Push Értesítések",
    ControlPermissions.PERMISSION_CONTROL_NOTIFICATIONS,
    listOf(),
    env
) {

    final override val allSettings by lazy {
        listOf(
            notificationsGroup,
            minRole,
            notificationsEnabled,
            permissionRequestGroup,
            permissionPromptText,
            permissionAcceptText,
            permissionDenyText,
            permissionAllowNeverShowAgain
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingRef(
        componentSettingService, component,
        "minRole", MinRoleSettingRef.ALL_ROLES,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )


    val notificationsGroup = ControlGroup(component, "notificationsGroup", fieldName = "Értesítés beállítások")

    val notificationsEnabled = BooleanSettingRef(componentSettingService, component,
        "notificationsEnabled", true, fieldName = "Értesítések engedélyezése a felhasználói felületen",
        description = "A felhasználók csak akkor kapnak push notificationokat, ha ez az opció engedélyezve van"
    )

    val permissionRequestGroup = ControlGroup(component, "permissionRequestGroup",
        fieldName = "Jogosultságkérés beállítások")

    val permissionPromptText = StringSettingRef(
        componentSettingService, component,
        "permissionPromptText", "Szeretnél értesítéseket kapni?", fieldName = "Engedélykérés szövege",
        description = "Ne legyen hosszú, mert csúnyán néz ki mobilokon! Ez a szöveg jelenik meg, amikor az alkalmazás engedélyt kér a felhasználótól értesítésekhez."
    )

    val permissionAcceptText = StringSettingRef(
        componentSettingService, component,
        "permissionAcceptText", "Igen", fieldName = "Engedély megadás gomb szöveg",
        description = "Ez a szöveg jelenik meg azon a gombon, amivel engedélyt tudnak adni a felhasználók"
    )

    val permissionDenyText = StringSettingRef(
        componentSettingService, component,
        "permissionDenyText", "Nem", fieldName = "Események tiltása gomb szöveg",
        description = "Ez a szöveg jelenik meg azon a gombon, amivel letiltják az értesítéseket a felhasználók (ha üres nem jelenik meg)"
    )

    val permissionAllowNeverShowAgain = BooleanSettingRef(
        componentSettingService, component,
        "permissionAllowNeverShowAgain", true, fieldName = "Tiltás megjegyzése",
        description = "Ha a felhasználó letiltotta az értesítéseket, akkor többet nem nem kérdez rá az alkalmazás"
    )

}
