package hu.bme.sch.cmsch.component.support

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(SupportComponent::class)
@EntityScan(basePackageClasses = [SupportComponent::class])
class SupportComponentEntityConfiguration
