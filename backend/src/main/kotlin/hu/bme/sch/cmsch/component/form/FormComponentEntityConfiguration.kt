package hu.bme.sch.cmsch.component.form

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(FormComponent::class)
@EntityScan(basePackageClasses = [FormComponent::class])
class FormComponentEntityConfiguration
