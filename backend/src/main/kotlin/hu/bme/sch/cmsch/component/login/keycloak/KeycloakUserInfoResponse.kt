package hu.bme.sch.cmsch.component.login.keycloak

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class KeycloakUserInfoResponse(
        var sid: String = "",

        var name: String = "",

        @set:JsonProperty("preferred_username")
        var preferredUsername: String = "",

        @set:JsonProperty("given_name")
        var givenName: String = "",

        @set:JsonProperty("family_name")
        var familyName: String = "",

        var email: String = "",

        @set:JsonProperty(required = false)
        @get:JsonProperty(required = false)
        var groups: List<String> = mutableListOf(),
)