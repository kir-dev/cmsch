package hu.bme.sch.cmsch.component.login.authsch

/**
 * Access Scope based on https://git.sch.bme.hu/kszk/authsch/wikis/api
 */
enum class Scope(val scope: String) {
    BASIC("basic"),
    DISPLAY_NAME("displayName"),
    SURNAME("sn"),
    GIVEN_NAME("givenName"),
    MAIL("mail"),
    NEPTUN_CODE("niifPersonOrgID"),
    LINKED_ACCOUNTS("linkedAccounts"),
    EDU_PERSON_ENTILEMENT("eduPersonEntitlement"),
    ROOM_NUMBER("roomNumber"),
    MOBILE("mobile"),
    COURSES("niifEduPersonAttendedCourse"),
    ENTRANTS("entrants"),
    ACTIVE_DIRECTORY_MEMBERSHIP("admembership"),
    BME_UNIT_SCOPE("bmeunitscope"),
    PERMANENT_ADDRESS("permanentaddress");

    companion object {
        fun byNameOrNull(name: String): Scope? {
            for (v in values())
                if (v.name == name)
                    return v
            return null
        }
    }
}
