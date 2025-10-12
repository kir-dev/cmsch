package hu.bme.sch.cmsch.component.news

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(NewsComponent::class)
@EntityScan(basePackageClasses = [NewsComponent::class])
class NewsComponentEntityConfiguration
