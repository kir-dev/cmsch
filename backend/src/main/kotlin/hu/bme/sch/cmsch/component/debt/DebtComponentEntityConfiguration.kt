package hu.bme.sch.cmsch.component.debt

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(DebtComponent::class)
@EntityScan(basePackageClasses = [DebtComponent::class])
class DebtComponentEntityConfiguration
