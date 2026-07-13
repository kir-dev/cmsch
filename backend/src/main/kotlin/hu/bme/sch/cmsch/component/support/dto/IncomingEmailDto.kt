package hu.bme.sch.cmsch.component.support.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
data class IncomingEmailDto(
    val spf: String = "",
    val id: String = "",
    val date: String = "",
    val subject: String = "",
    @JsonProperty("resent_date") val resentDate: String = "",
    val body: Body = Body(),
    val addresses: Addresses = Addresses()
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Body(val text: String = "", val html: String = "")

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Addresses(
        val from: Address = Address(),
        val to: Address = Address(),
        @JsonProperty("resent_from") val resentFrom: Address = Address()
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Address(val address: String = "")
}
