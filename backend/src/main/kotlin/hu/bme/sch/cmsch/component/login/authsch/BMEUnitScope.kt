package hu.bme.sch.cmsch.component.login.authsch

enum class BMEUnitScope(
    val bme: Boolean,
    val vik: Boolean,
    val vbk: Boolean,
    val active: Boolean,
    val newbie: Boolean
) {
    BME(true, false, false,false, false),
    BME_VIK(true, true, false,false, false),
    BME_ACTIVE(true, false, false,true, false),
    BME_NEWBIE(true, false, false,false, true),
    BME_VIK_ACTIVE(true, true, false,true, false),
    BME_VIK_NEWBIE(true, true, false,true, true),
    BME_VBK(true, false, true,false, false),
    BME_VBK_ACTIVE(true, false, true,true, false),
    BME_VBK_NEWBIE(true, false, true,true, true)
}
