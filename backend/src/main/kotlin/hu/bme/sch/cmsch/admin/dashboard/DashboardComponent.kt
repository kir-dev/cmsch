package hu.bme.sch.cmsch.admin.dashboard

interface DashboardComponent {

    val id: Int
    val title: String
    val type: String
    val wide: Boolean

}

fun pascalToKebab(input: String) = input.mapIndexed { index, c ->
    if (c.isUpperCase() && index != 0) "-${c.lowercaseChar()}" else c.lowercaseChar().toString()
}.joinToString("")
