package hu.bme.sch.cmsch.component.riddle

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(RiddleComponent::class)
@EntityScan(basePackageClasses = [RiddleComponent::class])
class RiddleComponentEntityConfiguration
