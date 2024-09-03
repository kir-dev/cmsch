package hu.bme.sch.cmsch.component.team

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamIntroductionRepository :
    CrudRepository<TeamIntroductionEntity, Int>,
    EntityPageDataSource<TeamIntroductionEntity, Int> {

    @Query("SELECT t FROM TeamIntroductionEntity t WHERE t.group.id = :groupId ORDER BY t.creationDate DESC")
    fun findIntroductionsForGroup(groupId: Int): List<TeamIntroductionEntity>

    @Query("SELECT t FROM TeamIntroductionEntity t WHERE t.group.id = :groupId and t.approved ORDER BY t.creationDate DESC")
    fun findApprovedIntroductionsForGroup(groupId: Int): List<TeamIntroductionEntity>

}
