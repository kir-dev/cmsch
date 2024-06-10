package hu.bme.sch.cmsch.component.gallery

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnBean(GalleryComponent::class)
@EntityScan(basePackageClasses = [GalleryComponent::class])
class GalleryComponentEntityConfiguration
