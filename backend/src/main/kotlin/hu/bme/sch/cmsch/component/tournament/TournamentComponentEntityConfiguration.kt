package hu.bme.sch.cmsch.component.tournament

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration


@Configuration
@ConditionalOnBean(TournamentComponent::class)
@EntityScan(basePackageClasses = [TournamentComponent::class])
class TournamentComponentEntityConfiguration
