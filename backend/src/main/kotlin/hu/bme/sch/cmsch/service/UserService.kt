package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.repository.GroupToUserMappingRepository
import hu.bme.sch.cmsch.repository.GuildToUserMappingRepository
import hu.bme.sch.cmsch.repository.UserRepository
import hu.bme.sch.cmsch.dto.virtual.GroupMemberVirtualEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class UserService(
    private val users: UserRepository,
    private val groupMapping: GroupToUserMappingRepository,
    private val guildMapping: GuildToUserMappingRepository
) {

    @Transactional(readOnly = false, isolation = Isolation.SERIALIZABLE)
    open fun save(user: UserEntity) {
        users.save(user)
    }

    @Transactional(readOnly = true)
git    open fun getById(id: String): UserEntity = users.findByInternalId(id).orElseThrow()

    @Transactional(readOnly = true)
    open fun findById(internalId: String): Optional<UserEntity> {
        return users.findByInternalId(internalId)
    }

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
                .map { GroupMemberVirtualEntity(0, it.fullName, it.neptun, it.guild.displayName, if (it.role.value <= RoleType.BASIC.value) "Résztvevő" else "Rendező" ) }

        return (mappings + users)
                .groupBy { it.neptun }
                .mapValues { (_, values) -> values.maxByOrNull { it.roleName }!! }
                .values
                .sortedBy { it.name }

    }

    private fun findGuildFor(neptun: String): String {
        return guildMapping.findByNeptun(neptun)
            .map { it.guild.displayName }
            .orElse("-")
    }

}
