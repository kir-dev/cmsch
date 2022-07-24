package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : CrudRepository<UserEntity, Int> {
    fun findByInternalId(internalId: String): Optional<UserEntity>
    fun existsByInternalId(internalId: String): Boolean
    fun findByCmschId(cmschId: String): Optional<UserEntity>
    fun findByNeptun(neptun: String): Optional<UserEntity>
    fun findAllByGroupName(groupName: String): List<UserEntity>
    fun findByEmail(email: String): Optional<UserEntity>
    fun findAllByFullName(fullName: String): List<UserEntity>
}
