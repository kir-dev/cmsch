package hu.bme.sch.cmsch.admin.setup

enum class SetupTextIcon {
    NONE,
    BOOL_ON,
    BOOL_OF,
    WHAT_YOU_SEE,
    INFO,
    SUCCESS,
    WARNING,
    ERROR,
    FLAG,
}

data class SetupTextArea(
    val text: String,
    val icon: SetupTextIcon,
)