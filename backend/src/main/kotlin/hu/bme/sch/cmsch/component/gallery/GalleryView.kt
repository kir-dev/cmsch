package hu.bme.sch.cmsch.component.gallery

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview

data class GalleryView(
    @field:JsonView(value = [ Preview::class, FullDetails::class ])
    val photos: List<GalleryEntity>
)