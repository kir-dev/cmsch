package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.GroupEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface GroupRepository : CrudRepository<GroupEntity, Int>,
    EntityPageDataSource<GroupEntity, Int> {

    fun findByName(name: String): Optional<GroupEntity>
    fun findByNameIgnoreCase(name: String): Optional<GroupEntity>
    fun findAllBySelectableTrue(): List<GroupEntity>
}
