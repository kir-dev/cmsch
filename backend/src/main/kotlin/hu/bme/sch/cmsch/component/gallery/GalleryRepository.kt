package hu.bme.sch.cmsch.component.gallery

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(GalleryComponent::class)
interface GalleryRepository : CrudRepository<GalleryEntity, Int>, EntityPageDataSource<GalleryEntity, Int> {
    override fun findAll(): List<GalleryEntity>

}
