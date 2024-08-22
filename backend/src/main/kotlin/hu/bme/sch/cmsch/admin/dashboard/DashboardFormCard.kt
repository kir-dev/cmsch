package hu.bme.sch.cmsch.admin.dashboard

import hu.bme.sch.cmsch.component.form.FormElement

class DashboardFormCard(
    override val id: Int,
    override val wide: Boolean,
    override val title: String,
    val description: String = "",
    val content: List<FormElement>,
    val buttonCaption: String = "Küld",
    val buttonIcon: String = "check_circle",
    val method: String = "post",
    val action: String = "submit",
    val multipartForm: Boolean = false
) : DashboardComponent {

    override val type: String = pascalToKebab(javaClass.simpleName)

}

object FormElementBackendCustomType {
    const val HIDDEN = "hidden"
}