package hu.bme.sch.cmsch.component.bounty

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(BountyComponent::class)
@EntityScan(basePackageClasses = [BountyComponent::class])
class BountyComponentEntityConfiguration
