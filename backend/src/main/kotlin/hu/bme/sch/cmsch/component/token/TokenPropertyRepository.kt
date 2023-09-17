package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(TokenComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface TokenPropertyRepository : CrudRepository<TokenPropertyEntity, Int>,
    EntityPageDataSource<TokenPropertyEntity, Int> {

    override fun findAll(): List<TokenPropertyEntity>

    fun findAllByOwnerUser_Id(owner: Int): List<TokenPropertyEntity>

    fun findAllByToken_Id(token: Int): List<TokenPropertyEntity>

    fun findAllByToken_Type(type: String): List<TokenPropertyEntity>

    fun countAllByOwnerUser_IdAndToken_Type(owner: Int, type: String): Int

    fun countAllByOwnerUser_IdAndToken_TypeAndToken_ActiveTargetTrue(owner: Int, type: String): Int

    fun countAllByOwnerGroup_IdAndToken_Type(groupId: Int, type: String): Int

    fun countAllByOwnerGroup_IdAndToken_TypeAndToken_ActiveTargetTrue(groupId: Int, type: String): Int

    fun findAllByOwnerGroup_Id(owner: Int): List<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerUser_Id(token: String, ownerId: Int): Optional<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerGroup_Id(token: String, ownerId: Int): Optional<TokenPropertyEntity>

    fun countAllByOwnerGroup_Id(ownerId: Int): Int

    fun countAllByOwnerUser_Id(ownerId: Int): Int

}
