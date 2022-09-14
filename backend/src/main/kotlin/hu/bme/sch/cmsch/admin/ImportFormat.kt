package hu.bme.sch.cmsch.admin

import kotlin.reflect.KClass

const val IMPORT_RAW_TEXT = "text"
const val IMPORT_BOOLEAN = "boolean"
const val IMPORT_LONG = "long"
const val IMPORT_INT = "int"
const val IMPORT_ENUM = "enum"
const val IMPORT_LOB = "lob"
const val IMPORT_FLOAT = "float"

annotation class ImportFormat(
        val ignore: Boolean = true,
        val columnId: Int = 0,
        val type: String = IMPORT_RAW_TEXT,
        val defaultValue: String = "",
        val enumSource: KClass<*> = Nothing::class
)
