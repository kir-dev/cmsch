package hu.bme.sch.cmsch.service

import com.fasterxml.jackson.databind.ObjectMapper
import hu.bme.sch.cmsch.component.login.CmschUser
import hu.bme.sch.cmsch.dto.UserConfig
import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.dto.virtual.GroupMemberVirtualEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.UserSelectorView
import org.postgresql.util.PSQLException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class UserService(
    private val users: UserRepository,
    private val groupMapping: GroupToUserMappingRepository,
    private val guildMapping: GuildToUserMappingRepository,
    private val objectMapper: ObjectMapper
) {

    private val userConfigReader = objectMapper.readerFor(UserConfig::class.java)

    @Retryable(value = [ PSQLException::class ], maxAttempts = 5, backoff = Backoff(delay = 500L, multiplier = 1.5))
    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun save(user: UserEntity) {
        users.save(user)
    }

    @Transactional(readOnly = true)
    open fun getById(id: String): UserEntity = users.findByInternalId(id).orElseThrow()

    @Transactional(readOnly = true)
    open fun getByUserId(id: Int): UserEntity = users.findById(id).orElseThrow()

    @Transactional(readOnly = true)
    open fun findById(internalId: String): Optional<UserEntity> {
        return users.findByInternalId(internalId)
    }

    @Transactional(readOnly = true)
    open fun findAll(): Iterable<UserEntity> = users.findAll()

    @Transactional(readOnly = true)
    open fun findAllUserSelectorView(): Iterable<UserSelectorView> = users.findAllSelectorView()

    @Transactional(readOnly = true)
    open fun exists(id: String) = users.existsByInternalId(id)

    @Transactional(readOnly = true)
    open fun searchByCmschId(cmschId: String): Optional<UserEntity> {
        return users.findByCmschId(cmschId)
    }

    @Transactional(readOnly = true)
    open fun allMembersOfGroup(groupName: String): Collection<GroupMemberVirtualEntity> {
        val mappings = groupMapping.findAllByGroupName(groupName)
                .asSequence()
                .map { GroupMemberVirtualEntity(0, it.fullName, it.neptun, findGuildFor(it.neptun), "-") }
        val users = users.findAllByGroupName(groupName)
                .asSequence()
                .map { GroupMemberVirtualEntity(0, it.fullName, it.neptun, it.guild.displayName, resolveRoleStatus(it)) }

        return (mappings + users)
                .groupBy { it.neptun }
                .mapValues { (_, values) -> values.maxByOrNull { it.roleName }!! }
                .values
                .sortedBy { it.name }

    }

    private fun resolveRoleStatus(it: UserEntity) =
        if (it.role.value <= RoleType.BASIC.value) "Résztvevő"
        else if (it.role.value <= RoleType.ATTENDEE.value) "Jelentkezett"
        else "Rendező"

    private fun findGuildFor(neptun: String): String {
        return guildMapping.findByNeptun(neptun)
            .map { it.guild.displayName }
            .orElse("-")
    }

    fun resolveConfig(config: String): UserConfig {
        if (config.isBlank())
            return UserConfig()
        return userConfigReader.readValue(config)
    }

    @Transactional(readOnly = false)
    open fun saveUserConfig(user: CmschUser, config: UserConfig) {
        val configValue = objectMapper.writerFor(UserConfig::class.java).writeValueAsString(config)
        users.findById(user.id).ifPresent {
            it.config = configValue
            users.save(it)
        }
    }

}
