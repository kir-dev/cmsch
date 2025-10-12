package hu.bme.sch.cmsch.component.communities

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(CommunitiesComponent::class)
@EntityScan(basePackageClasses = [CommunitiesComponent::class])
class CommunitiesComponentEntityConfiguration
