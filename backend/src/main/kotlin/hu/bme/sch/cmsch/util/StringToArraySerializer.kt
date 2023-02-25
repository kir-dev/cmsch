package hu.bme.sch.cmsch.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.itextpdf.io.exceptions.IOException
import kotlin.jvm.Throws

class StringToArraySerializer : JsonSerializer<String>() {

    @Throws(IOException::class)
    override fun serialize(value: String, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartArray()
        if (value.isNotEmpty()) {
            for (part in value
                    .replace("\r", "")
                    .replace("\n", "")
                    .split(",")) {
                gen.writeString(part.replace("\"", "\\\"").trim())
            }
        }
        gen.writeEndArray()
    }

}