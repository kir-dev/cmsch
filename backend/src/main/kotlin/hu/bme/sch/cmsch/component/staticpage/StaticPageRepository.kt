package hu.bme.sch.cmsch.component.staticpage

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(StaticPageComponent::class)
interface StaticPageRepository : CrudRepository<StaticPageEntity, Int>, EntityPageDataSource<StaticPageEntity, Int> {
    fun findByUrl(path: String): Optional<StaticPageEntity>
    fun findByUrlAndVisibleTrue(path: String): Optional<StaticPageEntity>
}
