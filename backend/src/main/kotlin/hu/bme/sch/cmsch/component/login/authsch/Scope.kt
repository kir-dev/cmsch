package hu.bme.sch.cmsch.component.login.authsch


enum class Scope(val scope: String) {
    OPEN_ID("openid"),
    PROFILE("profile"),
    EMAIL("email"),
    SAM_ACCOUNT_NAME("directory.sch.bme.hu:sAMAccountName"),
    NEPTUN_CODE("bme.hu:niifPersonOrgID"),
    BME_UNIT_SCOPE("meta.bme.hu:unitScope"),
    PEK_PROFILE("pek.sch.bme.hu:profile");

    companion object {
        fun byNameOrNull(name: String): Scope? {
            for (v in entries)
                if (v.name == name)
                    return v
            return null
        }
    }
}
