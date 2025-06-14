package hu.bme.sch.cmsch.setting

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory

interface SettingSerializer<T> {
    fun serialize(value: T, strict: Boolean): String

    fun deserialize(value: String, defaultValue: T, strict: Boolean): T

}

object JsonSettingSerializer : SettingSerializer<List<Map<String, Any>>> {
    private val log = LoggerFactory.getLogger(javaClass)

    private val anyObjectReference = object : TypeReference<Map<String, Any>>() {}
    private val reader = jacksonObjectMapper().readerFor(anyObjectReference)
    private val writer = jacksonObjectMapper().writerFor(anyObjectReference)

    override fun deserialize(
        value: String,
        defaultValue: List<Map<String, Any>>,
        strict: Boolean
    ): List<Map<String, Any>> =
        runCatching { reader.readValues<Map<String, Any>>(value).asSequence().toList() }
            .fold(
                onSuccess = { it },
                onFailure = {
                    if (strict) {
                        log.error("Value {} cannot be converted to a JSON", value, it)
                        return@fold defaultValue
                    }
                    throw IllegalArgumentException("Value $value cannot be converted to a boolean", it)
                }

            )

    override fun serialize(value: List<Map<String, Any>>, strict: Boolean): String = writer.writeValueAsString(value)

}

object StringSettingSerializer : SettingSerializer<String> {
    override fun deserialize(value: String, defaultValue: String, strict: Boolean): String = value

    override fun serialize(value: String, strict: Boolean): String = value

}

object BooleanSettingSerializer : SettingSerializer<Boolean> {
    private val log = LoggerFactory.getLogger(javaClass)

    private fun strictParse(value: String): Boolean? =
        if (value.equals("true", ignoreCase = true)) true
        else if (value.equals("false", ignoreCase = true)) false
        else null


    override fun deserialize(value: String, defaultValue: Boolean, strict: Boolean): Boolean {
        val parsed = strictParse(value)

        if (parsed != null) {
            return parsed
        }

        if (strict) {
            throw IllegalArgumentException("Value $value cannot be converted to a boolean")
        }

        log.error("Value {} cannot be converted to a boolean", value)
        return defaultValue
    }

    override fun serialize(value: Boolean, strict: Boolean): String = if (value) "true" else "false"

}

object LongSettingSerializer : SettingSerializer<Long> {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun deserialize(value: String, defaultValue: Long, strict: Boolean): Long {
        val parsed = value.toLongOrNull()
        if (parsed != null) {
            return parsed
        }

        if (strict) {
            throw IllegalArgumentException("Value $value cannot be converted to a long")
        }

        log.error("Value {} cannot be converted to a long", value)
        return defaultValue
    }

    override fun serialize(value: Long, strict: Boolean): String = value.toString()

}
