package hu.bme.sch.cmsch.admin

enum class IconStatus(
    val icon: String,
    val color: String,
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
    NEW("fiber_new", "#39C392"),
    RED_FLAG("flag", "#c84848"),
    HAPPY_FACE("sentiment_very_satisfied", "#39C392"),
    NEUTRAL_FACE("sentiment_neutral", "#f1b962"),
    DEAD_FACE("sentiment_very_dissatisfied", "#c84848"),
    COLOR_WHITE("humidity_high", "#ffffff"),
    COLOR_RED("humidity_high", "#c84848"),
    COLOR_BLUE("humidity_high", "#0ba6c1"),
    COLOR_YELLOW("humidity_high", "#f1b962"),
    COLOR_BLACK("humidity_low", "#ffffff"),
    TARGET_BLUE("target", "#0f9cb8"),
    TARGET_GREEN("target", "#0fb823"),
    TARGET_ORANGE("target", "#e9a107"),
    TARGET_PINK("target", "#ee8af5"),

    EMPTY("", ""),
    ;
    companion object {
        @JvmStatic
        fun names() = entries.map { it.name }

        @JvmStatic
        fun icons() = entries.map { it.icon }

        @JvmStatic
        fun colors() = entries.map { it.color }
    }

}
