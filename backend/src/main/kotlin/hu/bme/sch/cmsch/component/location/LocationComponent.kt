package hu.bme.sch.cmsch.component.location

import hu.bme.sch.cmsch.component.*
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["location"],
    havingValue = "true",
    matchIfMissing = false
)
class LocationComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("location", "/", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

            colorGroup,
            defaultGroupColor,
            blackGroupName,
            blueGroupName,
            cyanGroupName,
            pinkGroupName,
            orangeGroupName,
            greenGroupName,
            redGroupName,
            whiteGroupName,
            yellowGroupName,
            purpleGroupName,
            grayGroupName,

            appGroup,
            apkUrl
        )
    }

    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val colorGroup = SettingProxy(componentSettingService, component,
        "colorGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Csoport színek", serverSideOnly = true,
        description = ""
    )

    val defaultGroupColor = SettingProxy(componentSettingService, component,
        "defaultGroupColor", "#FF0000", type = SettingType.TEXT,
        fieldName = "Alapértelmezett csoport színe", serverSideOnly = true
    )

    val blackGroupName = SettingProxy(componentSettingService, component,
        "blackGroupName", "LEAD", type = SettingType.TEXT,
        fieldName = "Fekete csoport színe", serverSideOnly = true
    )

    val blueGroupName = SettingProxy(componentSettingService, component,
        "blueGroupName", "SENIOR", type = SettingType.TEXT,
        fieldName = "Kék csoport neve", serverSideOnly = true
    )

    val cyanGroupName = SettingProxy(componentSettingService, component,
        "cyanGroupName", "CONTROL", type = SettingType.TEXT,
        fieldName = "Türkiz csoport neve", serverSideOnly = true
    )

    val pinkGroupName = SettingProxy(componentSettingService, component,
        "pinkGroupName", "SUPPORT", type = SettingType.TEXT,
        fieldName = "Rózsaszín csoport neve", serverSideOnly = true
    )

    val orangeGroupName = SettingProxy(componentSettingService, component,
        "orangeGroupName", "KIRDEV", type = SettingType.TEXT,
        fieldName = "Narancs csoport neve", serverSideOnly = true
    )

    val greenGroupName = SettingProxy(componentSettingService, component,
        "greenGroupName", "", type = SettingType.TEXT,
        fieldName = "Zöld csoport neve", serverSideOnly = true
    )

    val redGroupName = SettingProxy(componentSettingService, component,
        "redGroupName", "", type = SettingType.TEXT,
        fieldName = "Piros csoport neve", serverSideOnly = true
    )

    val whiteGroupName = SettingProxy(componentSettingService, component,
        "whiteGroupName", "", type = SettingType.TEXT,
        fieldName = "Fehér csoport neve", serverSideOnly = true
    )

    val yellowGroupName = SettingProxy(componentSettingService, component,
        "yellowGroupName", "", type = SettingType.TEXT,
        fieldName = "Sárga csoport neve", serverSideOnly = true
    )

    val purpleGroupName = SettingProxy(componentSettingService, component,
        "purpleGroupName", "", type = SettingType.TEXT,
        fieldName = "Lila csoport neve", serverSideOnly = true
    )

    val grayGroupName = SettingProxy(componentSettingService, component,
        "grayGroupName", "", type = SettingType.TEXT,
        fieldName = "Szürke csoport neve", serverSideOnly = true
    )

    /// -------------------------------------------------------------------------------------------------------------------

    val appGroup = SettingProxy(componentSettingService, component,
        "appGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Tracker alkalmazás",
        description = "", serverSideOnly = true
    )

    val apkUrl = SettingProxy(componentSettingService, component,
        "apkUrl", "/files/cmsch-tracker-1.0.0.apk", type = SettingType.TEXT,
        serverSideOnly = true, fieldName = "APK URL-je"
    )


}
