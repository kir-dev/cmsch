package hu.bme.sch.g7.dao

import hu.bme.sch.g7.model.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AchievementRepository : CrudRepository<AchievementEntity, Int> {
    fun findAllByHighlightedTrue(): List<AchievementEntity>
}

@Repository
interface EventRepository : CrudRepository<EventEntity, Int> {
    override fun findAll(): List<EventEntity>
    fun findByUrl(url: String): Optional<EventEntity>
}

@Repository
interface GroupRepository : CrudRepository<GroupEntity, Int> {
}

@Repository
interface NewsRepository : CrudRepository<NewsEntity, Int> {

    fun findTop4ByOrderByTimestamp(): List<NewsEntity>

}

@Repository
interface UserRepository : CrudRepository<UserEntity, Int> {
}

@Repository
interface ExtraPageRepository : CrudRepository<ExtraPageEntity, Int> {
    fun findByUrl(path: String): Optional<ExtraPageEntity>
}

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Int> {
    fun findAllByTypeAndVisibleTrue(type: ProductType): List<ProductEntity>
}

@Repository
interface SoldProductRepository : CrudRepository<SoldProductEntity, Int> {
    fun findAllByOwner_Id(id: Int): List<SoldProductEntity>
}

@Repository
interface SubmittedAchievementRepository : CrudRepository<SubmittedAchievementEntity, Int> {
    fun findByAchievement_IdAndGroupId(achievementId: Int, groupId: Int): Optional<SubmittedAchievementEntity>
}

@Repository
interface RealtimeConfigRepository : CrudRepository<RealtimeConfigEntity, Int> {
    fun findByKey(key: String): Optional<RealtimeConfigEntity>
}