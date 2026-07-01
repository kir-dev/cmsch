package hu.bme.sch.cmsch.component.admission

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(AdmissionComponent::class)
@EntityScan(basePackageClasses = [AdmissionComponent::class])
class AdmissionComponentEntityConfiguration
