package hu.bme.sch.cmsch.component.app

class MenuItem(
    var name: String,
    var url: String,
    var external: Boolean = false,
    val children: MutableList<MenuItem> = mutableListOf(),
)
