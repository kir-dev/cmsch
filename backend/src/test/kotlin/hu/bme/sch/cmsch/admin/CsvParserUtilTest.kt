package hu.bme.sch.cmsch.admin

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Edit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

enum class TestType {
    TYPE1,
    ANOTHER_ONE
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class TestData(
    @property:ImportFormat()
    val id: Int = 0,

    @property:ImportFormat()
    val type: TestType = TestType.TYPE1,

    @property:ImportFormat()
    val name: String = "",

    @JsonView(value = [ Edit::class ])
    val notInCsv: String = "",
)

class CsvParserUtilTest {

    @Test
    fun exportToCsv_ValidData_CorrectCsvWritten() {
        val testData = listOf(
            TestData(1, TestType.TYPE1, "John\nmulti \"line\"","yap"),
            TestData(2, TestType.ANOTHER_ONE, "Jane, and jane","yup")
        )
        val outputStream = ByteArrayOutputStream()
        val csvParserUtil = CsvParserUtil(TestData::class)

        csvParserUtil.exportToCsv(testData, outputStream)
        val csvOutput = outputStream.toString().trim()

        val expectedCsv = "id,name,type\n1,\"John\nmulti \"\"line\"\"\",TYPE1\n2,\"Jane, and jane\",ANOTHER_ONE"
        assertEquals(expectedCsv, csvOutput)
    }

    @Test
    fun importFromCsv_ValidCsv_CorrectDataRead() {
        val csvInput = "id,type,name\n1,TYPE1,\"John\nmulti \"\"line\"\"\"\n2,ANOTHER_ONE,\"Jane, and jane\""
        val inputStream = ByteArrayInputStream(csvInput.toByteArray())
        val csvParserUtil = CsvParserUtil(TestData::class)

        val testData = csvParserUtil.importFromCsv(inputStream)

        val expectedTestData = listOf(
            TestData(1, TestType.TYPE1, "John\nmulti \"line\"",""),
            TestData(2, TestType.ANOTHER_ONE, "Jane, and jane", "")
        )
        assertEquals(expectedTestData, testData)
    }

}