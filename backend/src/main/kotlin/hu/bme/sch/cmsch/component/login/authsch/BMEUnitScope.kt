package hu.bme.sch.cmsch.component.login.authsch

enum class BMEUnitScope(
    private val bme: Boolean,
    private val vik: Boolean,
    private val vbk: Boolean,
    private val active: Boolean,
    private val newbie: Boolean
) {
    BME(true, false, false,false, false),
    BME_VIK(true, true, false,false, false),
    BME_ACTIVE(true, false, false,true, false),
    BME_VIK_ACTIVE(true, true, false,true, false),
    BME_VIK_NEWBIE(true, true, false,true, true),
    BME_VBK(true, false, true,false, false),
    BME_VBK_ACTIVE(true, false, true,true, false),
    BME_VBK_NEWBIE(true, false, true,true, true)
}
