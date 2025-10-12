package hu.bme.sch.cmsch.component.conference

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ConferenceComponent::class)
@EntityScan(basePackageClasses = [ConferenceComponent::class])
class ConferenceComponentEntityConfiguration
