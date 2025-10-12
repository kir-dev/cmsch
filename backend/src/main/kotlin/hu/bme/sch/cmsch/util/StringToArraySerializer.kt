package hu.bme.sch.cmsch.util

import com.itextpdf.io.exceptions.IOException
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

class StringToArraySerializer : ValueSerializer<String>() {

    @Throws(IOException::class)
    override fun serialize(value: String, gen: JsonGenerator, ctxt: SerializationContext) {
        gen.writeStartArray()
        if (value.isNotEmpty()) {
            for (part in value.replace("\r", "").replace("\n", "").split(",")) {
                gen.writeString(part.replace("\"", "\\\"").trim())
            }
        }
        gen.writeEndArray()
    }


}
