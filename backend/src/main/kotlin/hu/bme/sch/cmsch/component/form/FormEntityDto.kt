package hu.bme.sch.cmsch.component.form

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import hu.bme.sch.cmsch.dto.FullDetails
import org.slf4j.LoggerFactory

val readerForFields: ObjectReader = ObjectMapper().readerForListOf(FormElement::class.java)

data class FormEntityDto(
    @field:JsonView(FullDetails::class)
    var name: String = "",

    @field:JsonView(FullDetails::class)
    var url: String = "",

    @field:JsonView(FullDetails::class)
    var formFields: List<FormElement> = listOf(),

    @field:JsonView(FullDetails::class)
    var availableFrom: Long = 0,

    @field:JsonView(FullDetails::class)
    var availableUntil: Long = 0
) {

    constructor(other: FormEntity) : this(other.name, other.url, mutableListOf(), other.availableFrom, other.availableUntil) {
        try {
            this.formFields = readerForFields.readValue(other.formJson)
        } catch (e: Throwable) {
            LoggerFactory.getLogger(javaClass).warn("Invalid json string: {}", other.formJson)
        }
    }

}
