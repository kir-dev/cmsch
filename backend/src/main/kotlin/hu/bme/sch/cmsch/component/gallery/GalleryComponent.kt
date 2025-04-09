package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.component.*
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.MinRoleSettingProxy
import hu.bme.sch.cmsch.setting.SettingProxy
import hu.bme.sch.cmsch.setting.SettingType
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnProperty(
    prefix = "hu.bme.sch.cmsch.component.load",
    name = ["gallery"],
    havingValue = "true",
    matchIfMissing = false
)
class GalleryComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    "gallery",
    "/gallery",
    "Galéria",
    ControlPermissions.PERMISSION_CONTROL_GALLERY,
    listOf(GalleryEntity::class),
    env
) {

    final override val allSettings by lazy {
        listOf(
            galleryGroup,
            title, menuDisplayName, minRole,
        )
    }

    val galleryGroup = SettingProxy(componentSettingService, component,
        "galleryGroup", "", type = SettingType.COMPONENT_GROUP, persist = false,
        fieldName = "Galéria",
        description = ""
    )

    final val title = SettingProxy(componentSettingService, component,
        "title", "Galéria",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában"
    )

    final override val menuDisplayName = SettingProxy(componentSettingService, component,
        "menuDisplayName", "Galéria", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek"
    )

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "",
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

}
