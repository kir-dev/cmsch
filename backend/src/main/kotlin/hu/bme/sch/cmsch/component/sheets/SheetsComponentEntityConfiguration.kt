package hu.bme.sch.cmsch.component.sheets

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(SheetsComponent::class)
@EntityScan(basePackageClasses = [SheetsComponent::class])
class SheetsComponentEntityConfiguration
