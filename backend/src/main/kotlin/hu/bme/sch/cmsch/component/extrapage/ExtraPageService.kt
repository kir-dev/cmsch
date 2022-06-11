package hu.bme.sch.cmsch.component.extrapage

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(ExtraPageComponent::class)
open class ExtraPageService(
    private val extraPageRepository: ExtraPageRepository
) {

    @Transactional(readOnly = true)
    open fun getAll(): MutableIterable<ExtraPageEntity> {
        return extraPageRepository.findAll()
    }

}
