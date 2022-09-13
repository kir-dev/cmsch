package hu.bme.sch.cmsch.component.login.authsch

import com.fasterxml.jackson.annotation.JsonProperty

data class ProfileResponse(
    @set:JsonProperty("internal_id")
    var internalId: String = "",

    var displayName: String? = null,

    @set:JsonProperty("sn")
    var surname: String? = null,

    var givenName: String? = null,

    @set:JsonProperty("mail")
    var email: String? = null,

    @set:JsonProperty("niifPersonOrgID")
    var neptun: String? = null,

    var linkedAccounts: Map<String, String>? = mapOf(),

    var lastSync: Map<String, Long>? = mapOf(),

    var eduPersonEntitlement: List<PersonEntitlement>? = listOf(),

    var roomNumber: String? = null,

    var mobile: String? = null,

    @set:JsonProperty("niifEduPersonAttendedCourse")
    var courses: List<String>? = null,

    var entrants: List<Entrant>? = listOf(),

    var admembership: List<String>? = listOf(),

    var bmeunitscope: List<BMEUnitScope>? = listOf(),
)
