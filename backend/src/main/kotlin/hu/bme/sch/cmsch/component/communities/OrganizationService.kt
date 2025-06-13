package hu.bme.sch.cmsch.component.communities

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@ConditionalOnBean(CommunitiesComponent::class)
class OrganizationService(
    private val organizationRepo: OrganizationRepository,
    private val communityRepo: CommunityRepository
) {

    @Transactional(readOnly = true)
    fun getOrganizationById(id: Int): Optional<OrganizationEntity> {
        return organizationRepo.findById(id)
    }

    @Transactional(readOnly = true)
    fun getCommunityById(id: Int): Optional<CommunityEntity> {
        return communityRepo.findById(id).map {  community ->
            community.resortName = organizationRepo.findById(community.resortId)
                .map { it.name }
                .orElse("")
            community
        }
    }

    @Transactional(readOnly = true)
    fun getOrganizations(): List<OrganizationEntity> {
        return organizationRepo.findAll()
    }

    @Transactional(readOnly = true)
    fun getCommunities(): List<CommunityEntity> {
        return communityRepo.findAll()
    }

}
