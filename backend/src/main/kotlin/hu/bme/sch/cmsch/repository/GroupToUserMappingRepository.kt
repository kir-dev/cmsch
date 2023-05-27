package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.GroupToUserMappingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupToUserMappingRepository : CrudRepository<GroupToUserMappingEntity, Int>,
    EntityPageDataSource<GroupToUserMappingEntity, Int> {

    fun findByNeptun(neptun: String): Optional<GroupToUserMappingEntity>
    fun findAllByGroupName(groupName: String): List<GroupToUserMappingEntity>
}