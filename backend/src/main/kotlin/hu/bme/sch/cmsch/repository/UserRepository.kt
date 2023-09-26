package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.GuildType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class UserSelectorView(
    val id: Int,
    val name: String,
    val alias: String,
    val provider: String,
    val email: String
) {
    val fullNameWithAlias: String
        get() = if (alias != "") "$name ($alias)" else name
}

data class UserHandlerView(
    val id: Int,
    val name: String,
    val alias: String,
    val neptun: String,
    val guild: GuildType,
    val groupName: String,
    val email: String
)

@Repository
interface UserRepository : CrudRepository<UserEntity, Int>,
    EntityPageDataSource<UserEntity, Int> {

    fun findByInternalId(internalId: String): Optional<UserEntity>
    fun existsByInternalId(internalId: String): Boolean
    fun findByCmschId(cmschId: String): Optional<UserEntity>
    fun findByNeptun(neptun: String): Optional<UserEntity>
    fun findAllByGroupName(groupName: String): List<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
    fun countAllByGroup(group: GroupEntity): Long

    @Query("SELECT NEW hu.bme.sch.cmsch.repository.UserSelectorView(e.id, e.fullName, e.alias, e.provider, e.email) FROM UserEntity e")
    fun findAllSelectorView(): List<UserSelectorView>

    @Query("SELECT NEW hu.bme.sch.cmsch.repository.UserHandlerView(e.id, e.fullName, e.alias, e.neptun, e.guild, e.groupName, e.email) FROM UserEntity e")
    fun findAllUserHandlerView(): List<UserHandlerView>

}
