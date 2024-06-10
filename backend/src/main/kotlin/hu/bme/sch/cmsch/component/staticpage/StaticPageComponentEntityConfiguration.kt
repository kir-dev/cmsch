package hu.bme.sch.cmsch.component.staticpage

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(StaticPageComponent::class)
@EntityScan(basePackageClasses = [StaticPageComponent::class])
class StaticPageComponentEntityConfiguration
