package hu.bme.sch.cmsch.repository;

import UserDetailsByInternalIdMappingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserDetailsByInternalIdMappingRepository : CrudRepository<UserDetailsByInternalIdMappingEntity, Int> {
    fun findByInternalId(id: String): Optional<UserDetailsByInternalIdMappingEntity>
}