package hu.bme.sch.cmsch.component.gallery

import jakarta.transaction.Transactional
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service

@Service
@ConditionalOnBean(GalleryComponent::class)
open class GalleryService(
    private val galleryRepository: GalleryRepository
) {
    @Transactional
    fun savePhoto(galleryEntity: GalleryEntity) = galleryRepository.save(galleryEntity)

    fun fetchAllPhotos(): List<GalleryEntity> = galleryRepository.findAll()
}