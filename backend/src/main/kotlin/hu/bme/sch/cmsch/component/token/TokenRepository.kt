package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(TokenComponent::class)
interface TokenRepository : CrudRepository<TokenEntity, Int>,
    EntityPageDataSource<TokenEntity, Int> {

    fun findAllByTokenAndVisibleTrue(token: String): List<TokenEntity>
    fun countAllByVisibleTrue(): Long
    fun countAllByTypeAndVisibleTrue(type: String): Long
}
