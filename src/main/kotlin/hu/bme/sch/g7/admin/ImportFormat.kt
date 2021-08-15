package hu.bme.sch.g7.admin

import kotlin.reflect.KClass

const val IMPORT_RAW_TEXT = "text"
const val IMPORT_BOOLEAN = "boolean"
const val IMPORT_LONG = "long"
const val IMPORT_ENUM = "enum"

annotation class ImportFormat(
        val ignore: Boolean = true,
        val columnId: Int = 0,
        val type: String = IMPORT_RAW_TEXT,
        val defaultValue: String = "",
        val enumSource: KClass<*> = Nothing::class
)
