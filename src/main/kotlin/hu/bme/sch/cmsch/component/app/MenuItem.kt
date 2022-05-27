package hu.bme.sch.cmsch.component.app

class MenuItem(
    var url: String,
    var name: String,
    var external: Boolean = false,
    val children: MutableList<MenuItem> = mutableListOf(),
)
