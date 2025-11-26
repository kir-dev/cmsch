package hu.bme.sch.cmsch.component.key

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(AccessKeyComponent::class)
@EntityScan(basePackageClasses = [AccessKeyComponent::class])
class AccessKeyComponentEntityConfiguration
