package hu.bme.sch.cmsch.component.gallery

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@ConditionalOnBean(GalleryComponent::class)
class GalleryService(
    private val galleryRepository: GalleryRepository
) {
    @Transactional
    fun savePhoto(galleryEntity: GalleryEntity) = galleryRepository.save(galleryEntity)

    @Transactional(readOnly = true)
    fun fetchAllPhotos(): List<GalleryEntity> = galleryRepository.findAll()

    @Transactional(readOnly = true)
    fun fetchHomePagePhotos(): List<GalleryEntity> = galleryRepository.findByShowOnHomePage(true)
}
