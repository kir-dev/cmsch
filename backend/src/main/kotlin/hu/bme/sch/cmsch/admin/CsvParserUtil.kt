package hu.bme.sch.cmsch.admin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import java.io.InputStream
import java.io.OutputStream
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation

class CsvParserUtil<T : Any>(private val type: KClass<T>) {

    private val mapper = CsvMapper()
        .disable(CsvParser.Feature.FAIL_ON_MISSING_HEADER_COLUMNS)
        .disable(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(JsonGenerator.Feature.IGNORE_UNKNOWN)

    private val writerSchema: CsvSchema

    init {
        val schemaBuilder = CsvSchema.builder()
        type.declaredMemberProperties
            .filter { field ->
                field.hasAnnotation<ImportFormat>()
            }
            .forEach {
                schemaBuilder.addColumn(it.name)
            }
        schemaBuilder.setStrictHeaders(false)
        schemaBuilder.setUseHeader(true)
        schemaBuilder.setReorderColumns(true)

        writerSchema = schemaBuilder.build()
            .withColumnSeparator(',')
            .withEscapeChar('\\')
    }

    private val writer = mapper.writerFor(object : TypeReference<T>() {}).with(writerSchema)

    private val readerSchema: CsvSchema

    init {
        val schemaBuilder = CsvSchema.builder()
        type.declaredMemberProperties
            .filter { field ->
                field.hasAnnotation<ImportFormat>()
            }
            .forEach {
                schemaBuilder.addColumn(it.name)
            }
        schemaBuilder.setUseHeader(true)
        schemaBuilder.setReorderColumns(true)

        readerSchema = schemaBuilder.build()
            .withColumnSeparator(',')
            .withEscapeChar('\\')
    }

    private val reader = mapper.readerFor(type.java).with(readerSchema)

    fun exportToCsv(data: List<T>, outputStream: OutputStream) {
        writer.writeValue(outputStream, data)
    }

    fun importFromCsv(inputStream: InputStream): List<T> {
        return reader.readValues<T>(inputStream).readAll()
    }

}