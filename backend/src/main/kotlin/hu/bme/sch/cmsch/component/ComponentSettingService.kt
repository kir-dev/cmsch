package hu.bme.sch.cmsch.component

import hu.bme.sch.cmsch.component.task.TaskComponent
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(TaskComponent::class)
open class ComponentSettingService(
    private val componentSettingRepository: ComponentSettingRepository
) {

    @Transactional(readOnly = true)
    open fun refreshCachedSetting(setting: SettingProxy) {
        componentSettingRepository.findByComponentAndProperty(setting.component, setting.property)
            .orElse(null)
            ?.let { setting.setValue(it.value) }
    }

    @Transactional(readOnly = true)
    open fun refreshCachedSettings(settings: List<SettingProxy>) {
        settings.forEach(this::refreshCachedSetting)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun persistSetting(setting: SettingProxy) {
        if (!setting.persist)
            return

        componentSettingRepository.findByComponentAndProperty(setting.component, setting.property)
            .ifPresentOrElse({
                it.value = setting.rawValue
                componentSettingRepository.save(it)
            }, {
                componentSettingRepository.save(
                    ComponentSettingEntity(
                    component = setting.component,
                    property = setting.property,
                    value = setting.rawValue
                ))
            })
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun persistSettings(settings: List<SettingProxy>) {
        settings.forEach(this::persistSetting)
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun loadDefaultSetting(setting: SettingProxy) {
        if (!setting.persist)
            return

        componentSettingRepository.findByComponentAndProperty(setting.component, setting.property)
            .ifPresentOrElse({
                setting.setValue(it.value)
            }, {
                componentSettingRepository.save(
                    ComponentSettingEntity(
                    component = setting.component,
                    property = setting.property,
                    value = setting.rawValue
                ))
            })
    }

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun loadDefaultSettings(settings: List<SettingProxy>) {
        settings.forEach(this::loadDefaultSetting)
    }

}
