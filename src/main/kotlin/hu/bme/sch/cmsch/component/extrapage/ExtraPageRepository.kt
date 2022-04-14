package hu.bme.sch.cmsch.component.extrapage

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(ExtraPageComponent::class)
interface ExtraPageRepository : CrudRepository<ExtraPageEntity, Int> {
    fun findByUrlAndVisibleTrue(path: String): Optional<ExtraPageEntity>
}
