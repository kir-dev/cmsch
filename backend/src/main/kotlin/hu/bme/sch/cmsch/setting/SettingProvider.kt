package hu.bme.sch.cmsch.setting

import java.util.*

interface SettingProvider {

    fun getValue(setting: SettingProxy): Optional<String>

    fun setValue(setting: SettingProxy, value: String)

}
