package hu.bme.sch.cmsch.component.login.google

import com.fasterxml.jackson.annotation.JsonProperty

data class GoogleUserInfoResponse(
    var sub: String = "",

    var name: String = "",

    @set:JsonProperty("given_name")
    var givenName: String = "",

    @set:JsonProperty("family_name")
    var familyName: String = "",

    var picture: String = "",

    var email: String = "",

    @set:JsonProperty("email_verified")
    var emailVerified: Boolean = false,

    var locale: String = ""
) {

    val internalId: String
        get() = "google_${sub}"

}
