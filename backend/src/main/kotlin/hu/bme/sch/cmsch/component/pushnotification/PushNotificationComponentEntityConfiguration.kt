package hu.bme.sch.cmsch.component.pushnotification

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(PushNotificationComponent::class)
@EntityScan(basePackageClasses = [PushNotificationComponent::class])
class PushNotificationComponentEntityConfiguration
