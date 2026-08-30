package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.RoleToUserMappingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleToUserMappingRepository : CrudRepository<RoleToUserMappingEntity, Int>,
    EntityPageDataSource<RoleToUserMappingEntity, Int> {

    fun findByNeptun(neptun: String): Optional<RoleToUserMappingEntity>
    fun findByEmailIgnoreCase(email: String): Optional<RoleToUserMappingEntity>
}
