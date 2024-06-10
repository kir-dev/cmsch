package hu.bme.sch.cmsch.component.location

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(LocationComponent::class)
@EntityScan(basePackageClasses = [LocationComponent::class])
class LocationComponentEntityConfiguration
