package hu.bme.sch.cmsch.component.bmejegy

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(BmejegyComponent::class)
@EntityScan(basePackageClasses = [BmejegyComponent::class])
class BmejegyComponentEntityConfiguration
