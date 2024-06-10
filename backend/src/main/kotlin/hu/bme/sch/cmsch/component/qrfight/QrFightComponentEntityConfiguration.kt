package hu.bme.sch.cmsch.component.qrfight

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(QrFightComponent::class)
@EntityScan(basePackageClasses = [QrFightComponent::class])
class QrFightComponentEntityConfiguration
