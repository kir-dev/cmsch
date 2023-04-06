package hu.bme.sch.cmsch.component.challenge

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(ChallengeComponent::class)
interface ChallengeSubmissionRepository : CrudRepository<ChallengeSubmissionEntity, Int>,
    EntityPageDataSource<ChallengeSubmissionEntity, Int> {

}
