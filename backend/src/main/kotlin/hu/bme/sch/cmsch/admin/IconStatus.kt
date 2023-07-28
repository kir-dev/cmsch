package hu.bme.sch.cmsch.admin

enum class IconStatus(
    private val icon: String,
    private val color: String,
) {
    CROSS("close", "#c84848"),
    TICK("done", "#39C392"),
    PENDING("pending", "#0ba6c1"),
    WARNING("warning", "#f1b962"),
    REPORT("report", "#c84848"),
    ADMIN("local_police", "#0ba6c1"),
    STAR("star", "#eaa12f"),
    FLAG("flag", "#a439c3"),
    QUESTION("question_mark", "#eeeeee"),
    EMPTY("", "")
    ;

    companion object {
        @JvmStatic
        fun names() = values().map { it.name }

        @JvmStatic
        fun icons() = values().map { it.icon }

        @JvmStatic
        fun colors() = values().map { it.color }
    }

}