package hu.bme.sch.cmsch.component.script

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(ScriptComponent::class)
@EntityScan(basePackageClasses = [ScriptComponent::class])
class ScriptComponentEntityConfiguration
