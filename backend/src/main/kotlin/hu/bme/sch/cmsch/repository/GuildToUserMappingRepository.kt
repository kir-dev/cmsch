package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.GuildToUserMappingEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GuildToUserMappingRepository : CrudRepository<GuildToUserMappingEntity, Int>,
    EntityPageDataSource<GuildToUserMappingEntity, Int> {

    fun findByNeptun(neptun: String): Optional<GuildToUserMappingEntity>
}