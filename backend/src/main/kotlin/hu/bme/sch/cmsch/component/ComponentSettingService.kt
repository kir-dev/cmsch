package hu.bme.sch.cmsch.component

import org.postgresql.util.PSQLException
import org.springframework.context.ApplicationEventPublisher
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
open class ComponentSettingService(
    private val componentSettingRepository: ComponentSettingRepository,
    private val publisher: ApplicationEventPublisher
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

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
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

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun persistSettings(settings: List<SettingProxy>) {
        settings.forEach(this::persistSetting)
    }

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
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

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun loadDefaultSettings(settings: List<SettingProxy>) {
        settings.forEach(this::loadDefaultSetting)
    }

    fun onPersisted() {
        publisher.publishEvent(ComponentSettingsPersistedEvent(this))
    }

}
