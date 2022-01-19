package hu.bme.sch.cmsch.dao

import hu.bme.sch.cmsch.model.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AchievementRepository : CrudRepository<AchievementEntity, Int> {
    fun findAllByHighlightedTrueAndVisibleTrue(): List<AchievementEntity>
    fun findAllByVisibleTrue(): List<AchievementEntity>
}

@Repository
interface AchievementCategoryRepository : CrudRepository<AchievementCategoryEntity, Int> {
    override fun findAll(): List<AchievementCategoryEntity>
    fun findAllByCategoryId(categoryId: Int): List<AchievementCategoryEntity>
}

@Repository
interface EventRepository : CrudRepository<EventEntity, Int> {
    override fun findAll(): List<EventEntity>
    fun findAllByVisibleTrueOrderByTimestampStart(): List<EventEntity>
    fun findByUrl(url: String): Optional<EventEntity>
}

@Repository
interface GroupRepository : CrudRepository<GroupEntity, Int> {
    fun findByName(name: String): Optional<GroupEntity>
}

@Repository
interface NewsRepository : CrudRepository<NewsEntity, Int> {
    fun findTop4ByVisibleTrueOrderByTimestampDesc(): List<NewsEntity>
    fun findAllByVisibleTrueOrderByTimestampDesc(): List<NewsEntity>
}

@Repository
interface UserRepository : CrudRepository<UserEntity, Int> {
    fun findByPekId(pekId: String): Optional<UserEntity>
    fun existsByPekId(pekId: String): Boolean
    fun findByG7id(g7id: String): Optional<UserEntity>
    fun findByNeptun(neptun: String): Optional<UserEntity>
    fun findAllByGroupName(groupName: String): List<UserEntity>
}

@Repository
interface ExtraPageRepository : CrudRepository<ExtraPageEntity, Int> {
    fun findByUrlAndVisibleTrue(path: String): Optional<ExtraPageEntity>
}

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Int> {
    override fun findAll(): List<ProductEntity>
    fun findAllByType(type: ProductType): List<ProductEntity>
    fun findAllByTypeAndVisibleTrue(type: ProductType): List<ProductEntity>
}

@Repository
interface SoldProductRepository : CrudRepository<SoldProductEntity, Int> {
    fun findAllByOwnerId(id: Int): List<SoldProductEntity>

    fun findAllByResponsibleGroupId(id: Int): List<SoldProductEntity>
}

@Repository
interface SubmittedAchievementRepository : CrudRepository<SubmittedAchievementEntity, Int> {
    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_IdAndGroupId(achievementId: Int, groupId: Int): Optional<SubmittedAchievementEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_Id(achievementId: Int): List<SubmittedAchievementEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByAchievement_IdAndRejectedIsFalseAndApprovedIsFalse(achievementId: Int): List<SubmittedAchievementEntity>

    fun findAllByScoreGreaterThanAndApprovedIsTrue(zero: Int): List<SubmittedAchievementEntity>

    fun findAllByGroupId(groupId: Int): List<SubmittedAchievementEntity>
}

@Repository
interface RealtimeConfigRepository : CrudRepository<RealtimeConfigEntity, Int> {
    fun findByKey(key: String): Optional<RealtimeConfigEntity>
}

@Repository
interface GuildToUserMappingRepository : CrudRepository<GuildToUserMappingEntity, Int> {
    fun findByNeptun(neptun: String): Optional<GuildToUserMappingEntity>
}

@Repository
interface GroupToUserMappingRepository : CrudRepository<GroupToUserMappingEntity, Int> {
    fun findByNeptun(neptun: String): Optional<GroupToUserMappingEntity>
    fun findAllByGroupName(groupName: String): List<GroupToUserMappingEntity>
}

@Repository
interface LocationRepository : CrudRepository<LocationEntity, Int> {
    fun findByUserId(userId: Int): Optional<LocationEntity>
    override fun findAll(): List<LocationEntity>
}

@Repository
interface RiddleRepository : CrudRepository<RiddleEntity, Int> {
    override fun findAll(): List<RiddleEntity>
    fun findAllByCategoryId(categoryId: Int): List<RiddleEntity>
}

@Repository
interface RiddleCategoryRepository : CrudRepository<RiddleCategoryEntity, Int> {
    fun findAllByVisibleTrueAndMinRoleIn(roles: List<RoleType>): List<RiddleCategoryEntity>
    fun findByCategoryIdAndVisibleTrueAndMinRoleIn(categoryId: Int, roles: List<RoleType>): Optional<RiddleCategoryEntity>
}

@Repository
interface RiddleMappingRepository : CrudRepository<RiddleMappingEntity, Int> {
    fun findAllByOwnerUserAndCompletedTrue(user: UserEntity): List<RiddleMappingEntity>
    fun findAllByOwnerUserAndRiddle_CategoryId(user: UserEntity, categoryId: Int): List<RiddleMappingEntity>
    fun findByOwnerUserAndRiddle_Id(user: UserEntity, riddleId: Int): Optional<RiddleMappingEntity>
}

@Repository
interface TokenRepository : CrudRepository<TokenEntity, Int> {
    fun findAllByTokenAndVisibleTrue(token: String): List<TokenEntity>
}

@Repository
interface TokenPropertyRepository : CrudRepository<TokenPropertyEntity, Int> {

    override fun findAll(): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerUser_Id(owner: Int): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerGroup_Id(owner: Int): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByToken_TokenAndOwnerUser(token: String, owner: UserEntity): Optional<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByToken_TokenAndOwnerGroup(token: String, owner: GroupEntity): Optional<TokenPropertyEntity>

}
