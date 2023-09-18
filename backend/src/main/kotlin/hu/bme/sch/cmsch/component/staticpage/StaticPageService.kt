package hu.bme.sch.cmsch.component.staticpage

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(StaticPageComponent::class)
open class StaticPageService(
    private val staticPageRepository: StaticPageRepository
) {

    @Transactional(readOnly = true)
    open fun getAll(): Iterable<StaticPageEntity> {
        return staticPageRepository.findAll()
    }

    @Transactional(readOnly = true)
    open fun fetchSpecificPage(path: String) = staticPageRepository.findByUrlAndVisibleTrue(path)

}
