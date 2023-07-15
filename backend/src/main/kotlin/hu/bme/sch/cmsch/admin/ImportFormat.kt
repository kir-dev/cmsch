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
        @Deprecated("Use without the parameters")
        val ignore: Boolean = true,
        @Deprecated("Use without the parameters")
        val columnId: Int = 0,
        @Deprecated("Use without the parameters")
        val type: String = IMPORT_RAW_TEXT,
        @Deprecated("Use without the parameters")
        val defaultValue: String = "",
        @Deprecated("Use without the parameters")
        val enumSource: KClass<*> = Nothing::class
)
