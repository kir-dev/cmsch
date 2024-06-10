package hu.bme.sch.cmsch.component.race

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(RaceComponent::class)
@EntityScan(basePackageClasses = [RaceComponent::class])
class RaceComponentEntityConfiguration
