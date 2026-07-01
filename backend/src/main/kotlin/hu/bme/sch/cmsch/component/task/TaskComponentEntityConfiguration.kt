package hu.bme.sch.cmsch.component.task

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(TaskComponent::class)
@EntityScan(basePackageClasses = [TaskComponent::class])
class TaskComponentEntityConfiguration
