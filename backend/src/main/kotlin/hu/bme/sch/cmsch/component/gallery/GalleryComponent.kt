package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.service.ControlPermissions
import hu.bme.sch.cmsch.setting.ComponentSettingService
import hu.bme.sch.cmsch.setting.MinRoleSettingRef
import hu.bme.sch.cmsch.setting.SettingGroup
import hu.bme.sch.cmsch.setting.StringSettingRef
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
@ConditionalOnBooleanProperty(value = ["hu.bme.sch.cmsch.component.load.gallery"])
class GalleryComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase(
    componentSettingService,
    "gallery",
    "/gallery",
    "Galéria",
    ControlPermissions.PERMISSION_CONTROL_GALLERY,
    listOf(GalleryEntity::class),
    env
) {

    val galleryGroup by SettingGroup(fieldName = "Galéria")

    final var title by StringSettingRef("Galéria",
        fieldName = "Lap címe", description = "Ez jelenik meg a böngésző címsorában")

    final override var menuDisplayName by StringSettingRef("Galéria", serverSideOnly = true,
        fieldName = "Menü neve", description = "Ez lesz a neve a menünek")

    final override var minRole by MinRoleSettingRef(setOf(), fieldName = "Jogosultságok",
        description = "Melyik roleokkal nyitható meg az oldal")

}
