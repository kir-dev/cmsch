package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.TokenEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : CrudRepository<TokenEntity, Int> {
    fun findAllByTokenAndVisibleTrue(token: String): List<TokenEntity>
    fun findAllByTypeAndVisibleTrue(type: String): List<TokenEntity>
}