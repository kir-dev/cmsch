package hu.bme.sch.cmsch.component.challenge

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ChallengeComponent::class)
@EntityScan(basePackageClasses = [ChallengeComponent::class])
class ChallengeComponentEntityConfiguration
