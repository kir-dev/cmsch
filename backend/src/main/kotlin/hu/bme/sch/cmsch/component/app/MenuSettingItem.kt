package hu.bme.sch.cmsch.component.app

class MenuSettingItem(
    var id: String,
    var name: String,
    var url: String,
    var order: Int = 0,
    var visible: Boolean = false,
    var subMenu: Boolean = false,
    var external: Boolean = false,
) {
    fun toMenuItem() = MenuItem(name, url, external, mutableListOf())
}
