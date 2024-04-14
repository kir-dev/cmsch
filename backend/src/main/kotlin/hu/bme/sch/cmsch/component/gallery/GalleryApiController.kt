package hu.bme.sch.cmsch.component.gallery

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.Preview
import hu.bme.sch.cmsch.model.RoleType
import hu.bme.sch.cmsch.util.getUserOrNull
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(GalleryComponent::class)
class GalleryApiController(
    private val galleryComponent: GalleryComponent,
    private val galleryService: GalleryService
) {
    @JsonView(Preview::class)
    @GetMapping("/gallery")
    @Operation(
        summary = "List all photos for the authenticated user.",
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "List of photos in the gallery"),
        ApiResponse(responseCode = "403", description = "This endpoint is not available for the given auth header")
    ])
    fun gallery(auth: Authentication?): ResponseEntity<GalleryView> {
        val user = auth?.getUserOrNull()
        if (!galleryComponent.minRole.isAvailableForRole(user?.role ?: RoleType.GUEST))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()

        val photos = galleryService.fetchAllPhotos()

        return ResponseEntity.ok(GalleryView(photos = photos))
    }
}
