package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserEntity, Int> {
    fun findByPekId(pekId: String): Optional<UserEntity>
    fun existsByPekId(pekId: String): Boolean
    fun findByG7id(g7id: String): Optional<UserEntity>
    fun findByNeptun(neptun: String): Optional<UserEntity>
    fun findAllByGroupName(groupName: String): List<UserEntity>
}