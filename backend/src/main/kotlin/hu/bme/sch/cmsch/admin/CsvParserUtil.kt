package hu.bme.sch.cmsch.admin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class CsvParserUtil<T : Any>(private val type: KClass<T>) {

    private val mapper = CsvMapper()
        .apply { registerModule(KotlinModule.Builder().build()) }
        .disable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
        .disable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(JsonGenerator.Feature.IGNORE_UNKNOWN)

    private val writerSchema: CsvSchema

    init {
        val importFields = type.memberProperties.filter { it.findAnnotation<ImportFormat>() != null }

        val schemaBuilder = CsvSchema.builder()
        for (field in importFields) {
            schemaBuilder.addColumn(field.name)
        }
        writerSchema = schemaBuilder.build()
            .withColumnSeparator(',')
            .withEscapeChar('\\')
            .withHeader()
    }

    private val readerSchema: CsvSchema

    init {
        val schemaBuilder = CsvSchema.builder()
        val importFields = type.memberProperties.filter { it.findAnnotation<ImportFormat>() != null }

        for (field in importFields) {
            schemaBuilder.addColumn(field.name)
        }
        readerSchema = schemaBuilder.setReorderColumns(true).build()
            .withColumnSeparator(',')
            .withEscapeChar('\\')
            .withHeader()
    }

    fun exportToCsv(data: List<T>, outputStream: OutputStream) {
        val writer = mapper.writerFor(object : TypeReference<List<T>>() {}).with(writerSchema)
        return writer.writeValue(outputStream, data)
    }

    fun importFromCsv(inputStream: InputStream): List<T> {
        val reader = mapper.readerFor(type.java).with(readerSchema)
        return reader.readValues<T>(inputStream).readAll()
    }

}