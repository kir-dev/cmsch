package hu.bme.sch.cmsch.component.script.sandbox

import tools.jackson.module.kotlin.jacksonObjectMapper

class ScriptingJsonUtil {

    fun generatePrettyJson(value: Any): String {
        return jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(value)
    }

    fun generateJson(value: Any): String {
        return jacksonObjectMapper().writeValueAsString(value)
    }

}
