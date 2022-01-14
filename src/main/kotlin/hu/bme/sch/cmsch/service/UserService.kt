package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.dao.GroupToUserMappingRepository
import hu.bme.sch.cmsch.dao.GuildToUserMappingRepository
import hu.bme.sch.cmsch.dao.UserRepository
import hu.bme.sch.cmsch.dto.virtual.GroupMemberVirtualEntity
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Suppress("RedundantModalityModifier") // Spring transactional proxy requires it not to be final
@Service
open class UserService(
        val users: UserRepository,
        val groupMapping: GroupToUserMappingRepository,
        val guildMapping: GuildToUserMappingRepository
) {

    @Transactional
    open fun save(user: UserEntity) {
        users.save(user)
    }

    @Transactional(readOnly = true)
    open fun getById(id: String): UserEntity = users.findByPekId(id).orElseThrow()

    @Transactional(readOnly = true)
    open fun exists(id: String) = users.existsByPekId(id)

    @Transactional(readOnly = true)
    open fun searchByG7Id(g7id: String): Optional<UserEntity> {
        return users.findByG7id(g7id)
    }

    @Transactional(readOnly = true)
    open fun allMembersOfGroup(groupName: String): Collection<GroupMemberVirtualEntity> {
        val mappings = groupMapping.findAllByGroupName(groupName)
                .asSequence()
                .map { GroupMemberVirtualEntity(0, it.fullName, it.neptun, findGuildFor(it.neptun), "-") }
        val users = users.findAllByGroupName(groupName)
                .asSequence()
                .map { GroupMemberVirtualEntity(0, it.fullName, it.neptun, it.guild.displayName, if (it.role.value <= RoleType.BASIC.value) "Gólya" else "Rendező" ) }

        return (mappings + users)
                .groupBy { it.neptun }
                .mapValues { (_, values) -> values.sortedByDescending { it.roleName }.first() }
                .values
                .sortedBy { it.name }

    }

    private fun findGuildFor(neptun: String): String {
        return guildMapping.findByNeptun(neptun).map { it.guild.displayName }.orElse("-")
    }

}
