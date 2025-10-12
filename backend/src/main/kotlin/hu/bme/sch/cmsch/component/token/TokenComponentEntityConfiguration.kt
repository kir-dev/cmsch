package hu.bme.sch.cmsch.component.token

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(TokenComponent::class)
@EntityScan(basePackageClasses = [TokenComponent::class])
class TokenComponentEntityConfiguration
