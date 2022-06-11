package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.ComponentBase
import hu.bme.sch.cmsch.component.ComponentSettingService
import hu.bme.sch.cmsch.component.MinRoleSettingProxy
import hu.bme.sch.cmsch.model.RoleType
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class StylingComponent(
    componentSettingService: ComponentSettingService,
    env: Environment
) : ComponentBase("style", "/style", componentSettingService, env) {

    final override val allSettings by lazy {
        listOf(
            minRole,

        )
    }


    final override val menuDisplayName = null

    final override val minRole = MinRoleSettingProxy(componentSettingService, component,
        "minRole", "", minRoleToEdit = RoleType.NOBODY,
        fieldName = "Jogosultságok", description = "Melyik roleokkal nyitható meg az oldal"
    )

    /// -------------------------------------------------------------------------------------------------------------------

//    // Colors
//    var backgroundColor: String,
//    var textColor1: String,
//    var textColor2: String,
//    var textColorAccent: String,
//    var brandingColor1: String,
//    var brandingColor2: String,
//
//    // Background images
//    var backgroundUrl: String,
//    var mobileBackgroundUrl: String,
//
//    // Typography
//    var mainFontName: String,
//    var mainFontCdn: String,
//    var displayFontName: String,
//    var displayFontCdn: String,

}
