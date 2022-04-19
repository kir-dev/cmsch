package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.component.SettingProxy
import net.minidev.json.annotate.JsonIgnore

class MenuItem(
    @JsonIgnore
    var customName: String?,

    @JsonIgnore
    val source: SettingProxy?,

    var url: String,
    var menu: Boolean = false,
    var external: Boolean = false,
    val children: List<MenuItem> = listOf(),
) {

    val displayName: String
        get() = source?.getValue() ?: customName ?: "n/a"

}
