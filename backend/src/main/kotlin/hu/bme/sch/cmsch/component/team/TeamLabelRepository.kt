package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamLabelRepository : CrudRepository<TeamLabelEntity, Int>,
    EntityPageDataSource<TeamLabelEntity, Int> {

    @Query("select new hu.bme.sch.cmsch.component.team.TeamLabelView(e.id, e.name, e.color, e.desc) from TeamLabelEntity e")
    fun findAllThatExists(): List<TeamLabelView>

    @Query("select e from TeamLabelEntity e where :groupId = e.groupId")
    fun findByGroupId(groupId: Int): List<TeamLabelEntity>

    @Query("select new hu.bme.sch.cmsch.component.team.TeamLabelView(e.id, e.name, e.color, e.desc) from TeamLabelEntity e where e.groupId = :groupId")
    fun findByGroupIdView(groupId: Int): List<TeamLabelView>

    @Query("select new hu.bme.sch.cmsch.component.team.TeamLabelView(e.id, e.name, e.color, e.desc) from TeamLabelEntity e where e.groupId = :groupId and e.showList")
    fun findOnListByGroupId(groupId: Int): List<TeamLabelView>
}
