package hu.bme.sch.cmsch.component.communities

import com.fasterxml.jackson.annotation.JsonView
import hu.bme.sch.cmsch.dto.FullDetails
import hu.bme.sch.cmsch.dto.Preview
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = ["\${cmsch.frontend.production-url}"], allowedHeaders = ["*"])
@ConditionalOnBean(CommunitiesComponent::class)
class CommunitiesApiController(
    private val organizationService: OrganizationService
) {

    @JsonView(Preview::class)
    @GetMapping("/community")
    fun allCommunities(): List<CommunityEntity> {
        return organizationService.getCommunities()
            .filter { it.visible }
    }

    @JsonView(Preview::class)
    @GetMapping("/organization")
    fun allOrganizations(): List<OrganizationEntity> {
        return organizationService.getOrganizations()
            .filter { it.visible }
    }

    @JsonView(FullDetails::class)
    @GetMapping("/community/{communityId}")
    fun allCommunities(@PathVariable communityId: Int): CommunityEntity? {
        return organizationService.getCommunityById(communityId)
            .filter { it.visible }
            .orElse(null)
    }

    @JsonView(FullDetails::class)
    @GetMapping("/organization/{organizationId}")
    fun allOrganizations(@PathVariable organizationId: Int): OrganizationEntity? {
        return organizationService.getOrganizationById(organizationId)
            .filter { it.visible }
            .orElse(null)
    }

}