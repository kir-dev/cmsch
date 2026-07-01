package hu.bme.sch.cmsch.component.team

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan(basePackageClasses = [TeamComponent::class])
class TeamComponentEntityConfiguration
