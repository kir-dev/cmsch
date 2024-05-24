package hu.bme.sch.cmsch.component.pushnotification

import hu.bme.sch.cmsch.model.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessagingTokenRepository : JpaRepository<MessagingTokenEntity, Long> {

    @Query("select m.token from MessagingTokenEntity m")
    fun findAllTokens(): List<String>

    @Query("select m.token from MessagingTokenEntity m where m.userId = ?1")
    fun findAllTokensByUserId(userId: Int): List<String>

    @Query("select m.token from MessagingTokenEntity m inner join UserEntity u on u.id = m.userId where u.group.id = ?1")
    fun findAllTokensByGroupId(groupId: Int): List<String>

    @Query("select m.token from MessagingTokenEntity m inner join UserEntity u on u.id = m.userId where u.role = ?1")
    fun findAllTokensByRole(role: RoleType): List<String>

    fun deleteByUserIdAndToken(userId: Int, token: String): Long

    fun existsByUserIdAndToken(userId: Int, token: String): Boolean

    fun deleteByTokenIn(tokens: List<String>): Long
}
