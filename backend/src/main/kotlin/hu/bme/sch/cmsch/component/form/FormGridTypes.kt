package hu.bme.sch.cmsch.component.form

data class GridElement(
    val key: String,
    val label: String,
)

data class FormGridValue(
    val options: List<GridElement>,
    val questions: List<GridElement>,
)

data class ChoiceGridElement(
    val key: String,
    val value: String,
)
