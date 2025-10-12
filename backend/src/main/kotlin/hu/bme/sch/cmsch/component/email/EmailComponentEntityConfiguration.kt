package hu.bme.sch.cmsch.component.email

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(EmailComponent::class)
@EntityScan(basePackageClasses = [EmailComponent::class])
class EmailComponentEntityConfiguration
