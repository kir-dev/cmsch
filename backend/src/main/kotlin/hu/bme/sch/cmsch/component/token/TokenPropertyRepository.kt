package hu.bme.sch.cmsch.component.token

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Query
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

    fun findAllByOwnerUser_IdAndToken_Type(owner: Int, token: String): List<TokenPropertyEntity>

    fun findAllByOwnerGroup_IdAndToken_Type(owner: Int, type: String): List<TokenPropertyEntity>

    fun countAllByOwnerUser_IdAndToken_Type(owner: Int, type: String): Int

    fun countAllByOwnerUser_IdAndToken_TypeAndToken_ActiveTargetTrue(owner: Int, type: String): Int

    fun countAllByOwnerGroup_IdAndToken_Type(groupId: Int, type: String): Int

    fun countAllByOwnerGroup_IdAndToken_TypeAndToken_ActiveTargetTrue(groupId: Int, type: String): Int

    fun findAllByOwnerGroup_Id(owner: Int): List<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerUser_Id(token: String, ownerId: Int): Optional<TokenPropertyEntity>

    fun findByToken_TokenAndOwnerGroup_Id(token: String, ownerId: Int): Optional<TokenPropertyEntity>

    fun countAllByOwnerGroup_Id(ownerId: Int): Int

    fun countAllByOwnerUser_Id(ownerId: Int): Int

    @Query(
        "select new hu.bme.sch.cmsch.component.token.UserGroupTokenCount(coalesce(g.id, -1) , coalesce(g.name, 'n/a'), count(*), sum(p.token.score), coalesce(g.memberCount, (select count(*) from UserEntity u where u.group.id = g.id or (u.group.id is null and g.id is null))) ) " +
                "from GroupEntity g " +
                "         right outer join UserEntity u on u.group.id = g.id " +
                "         inner join TokenPropertyEntity p on u.id = p.ownerUser.id " +
                "group by g.id"
    )
    fun countByAllUserGroup(): List<UserGroupTokenCount>
}
