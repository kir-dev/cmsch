package hu.bme.sch.cmsch.component.event

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(EventComponent::class)
@EntityScan(basePackageClasses = [EventComponent::class])
class EventComponentEntityConfiguration
