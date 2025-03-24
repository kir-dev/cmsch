package hu.bme.sch.cmsch.component.serviceaccount

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ServiceAccountComponent::class)
@EntityScan(basePackageClasses = [ServiceAccountComponent::class])
class ServiceAccountComponentConfiguration
