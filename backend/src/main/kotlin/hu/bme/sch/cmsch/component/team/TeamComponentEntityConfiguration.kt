package hu.bme.sch.cmsch.component.team

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(TeamComponent::class)
@EntityScan(basePackageClasses = [TeamComponent::class])
class TeamComponentEntityConfiguration
