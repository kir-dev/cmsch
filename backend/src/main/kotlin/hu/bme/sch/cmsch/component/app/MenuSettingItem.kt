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

    override fun toString(): String {
        return "MenuSettingItem(id='$id', name='$name', url='$url', order=$order, " +
                "visible=$visible, subMenu=$subMenu, external=$external)"
    }

}
