package hu.bme.sch.cmsch.component.errorlog

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ErrorLogComponent::class)
@EntityScan(basePackageClasses = [ErrorLogComponent::class])
class ErrorLogComponentEntityConfiguration
