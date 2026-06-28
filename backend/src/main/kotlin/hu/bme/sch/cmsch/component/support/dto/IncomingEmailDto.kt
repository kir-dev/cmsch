package hu.bme.sch.cmsch.component.support.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class IncomingEmailDto(
    val spf: String = "",
    val id: String = "",
    val date: String = "",
    val subject: String = "",
    @JsonProperty("resent_date") val resentDate: String = "",
    val body: Body = Body(),
    val addresses: Addresses = Addresses()
) {
    data class Body(val text: String = "", val html: String = "")
    data class Addresses(
        val from: Address = Address(),
        val to: Address = Address(),
        @JsonProperty("resent_from") val resentFrom: Address = Address()
    )
    data class Address(val address: String = "")
}
