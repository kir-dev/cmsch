package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(RiddleComponent::class)
class RiddleMicroserviceDashboard {



}