package hu.bme.sch.cmsch.component.proto

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ProtoComponent::class)
@EntityScan(basePackageClasses = [ProtoComponent::class])
class ProtoComponentEntityConfiguration
