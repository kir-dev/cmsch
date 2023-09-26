package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.component.team.TeamListView
import hu.bme.sch.cmsch.model.GroupEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

data class GroupNameView(
    val name: String
)

@Repository
interface GroupRepository : CrudRepository<GroupEntity, Int>,
    EntityPageDataSource<GroupEntity, Int> {

    @Query("select new hu.bme.sch.cmsch.component.team.TeamListView(e.id, e.name) from GroupEntity e")
    fun findAllThatExists(): List<TeamListView>

    @Query("select new hu.bme.sch.cmsch.component.team.TeamListView(e.id, e.name) from GroupEntity e where e.races")
    fun findAllThatRaces(): List<TeamListView>

    @Query("select new hu.bme.sch.cmsch.component.team.TeamListView(e.id, e.name) from GroupEntity e where e.races and e.manuallyCreated")
    fun findAllThatRacesAndManuallyCreated(): List<TeamListView>

    @Query("select new hu.bme.sch.cmsch.component.team.TeamListView(e.id, e.name) from GroupEntity e where e.manuallyCreated")
    fun findAllThatManuallyCreated(): List<TeamListView>

    fun findByName(name: String): Optional<GroupEntity>
    fun findByNameIgnoreCase(name: String): Optional<GroupEntity>
    fun findAllBySelectableTrue(): List<GroupEntity>

    @Query("select new hu.bme.sch.cmsch.repository.GroupNameView(e.name) from GroupEntity e")
    fun findAllGroupNames(): List<GroupNameView>
}
