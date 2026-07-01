package hu.bme.sch.cmsch.component.script.sandbox

import tools.jackson.dataformat.csv.CsvMapper
import tools.jackson.dataformat.csv.CsvSchema

class ScriptingCsvUtil {

    fun generateCsv(
        content: List<List<String>>,
        columnSeperator: Char = ',',
        lineSeparator: String = "\n",
        quoteChar: Char = '"'
    ): String {
        val csvMapper = CsvMapper()
        val csvSchema = CsvSchema.builder()
            .setUseHeader(false)
            .setColumnSeparator(columnSeperator)
            .setLineSeparator(lineSeparator)
            .setQuoteChar(quoteChar)
            .build()
        return csvMapper.writer(csvSchema).writeValueAsString(content)
    }

    inline fun <reified T> generateTypedCsv(
        content: List<T>,
        columnSeperator: Char = ',',
        lineSeparator: String = "\n",
        quoteChar: Char = '"'
    ): String {
        val csvMapper = CsvMapper()
        val csvSchema = csvMapper.schemaFor(T::class.java)
            .withHeader()
            .withColumnSeparator(columnSeperator)
            .withLineSeparator(lineSeparator)
            .withQuoteChar(quoteChar)

        return csvMapper.writerFor(csvMapper.typeFactory.constructCollectionType(List::class.java, T::class.java))
            .with(csvSchema)
            .writeValueAsString(content)
    }

}
