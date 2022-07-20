package hu.bme.sch.cmsch.component.signup

import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import hu.bme.sch.cmsch.dto.FullDetails
import org.slf4j.LoggerFactory

val readerForFields: ObjectReader = ObjectMapper().readerForListOf(SignupFormElement::class.java)

data class SignupFormEntityDto(
    @JsonView(FullDetails::class)
    var name: String = "",

    @JsonView(FullDetails::class)
    var url: String = "",

    @JsonView(FullDetails::class)
    var formFields: List<SignupFormElement> = listOf(),

    @JsonView(FullDetails::class)
    var availableFrom: Long = 0,

    @JsonView(FullDetails::class)
    var availableUntil: Long = 0
) {

    constructor(other: SignupFormEntity) : this(other.name, other.url, mutableListOf(), other.availableFrom, other.availableUntil) {
        try {
            this.formFields = readerForFields.readValue(other.formJson)
        } catch (e: Throwable) {
            LoggerFactory.getLogger(javaClass).warn("Invalid json string: {}", other.formJson)
        }
    }

}
