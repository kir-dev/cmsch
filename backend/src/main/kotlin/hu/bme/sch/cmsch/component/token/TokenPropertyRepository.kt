package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.model.GroupEntity
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@ConditionalOnBean(TokenComponent::class)
@Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
interface TokenPropertyRepository : CrudRepository<TokenPropertyEntity, Int> {

    override fun findAll(): List<TokenPropertyEntity>

    fun findAllByOwnerUser_Id(owner: Int): List<TokenPropertyEntity>

    fun findAllByToken_Id(token: Int): List<TokenPropertyEntity>

    fun findAllByToken_Type(type: String): List<TokenPropertyEntity>

    fun findAllByOwnerUser_IdAndToken_Type(owner: Int, type: String): List<TokenPropertyEntity>

    fun findAllByOwnerUser_IdAndToken_TypeAndToken_ActiveTargetTrue(owner: Int, type: String): List<TokenPropertyEntity>

    fun findAllByOwnerGroup_IdAndToken_Type(groupId: Int, type: String): List<TokenPropertyEntity>

    fun findAllByOwnerGroup_IdAndToken_TypeAndToken_ActiveTargetTrue(groupId: Int, type: String): List<TokenPropertyEntity>

    fun findAllByOwnerGroup_Id(owner: Int): List<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerUser_Id(token: String, ownerId: Int): Optional<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerGroup(token: String, owner: GroupEntity): Optional<TokenPropertyEntity>

}
