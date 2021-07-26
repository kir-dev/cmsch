package hu.bme.sch.g7.dao

import hu.bme.sch.g7.model.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AchievementRepository : CrudRepository<AchievementEntity, Int> {
}

@Repository
interface EventRepository : CrudRepository<EventEntity, Int> {

    override fun findAll(): List<EventEntity>

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
}

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Int> {
}

@Repository
interface SoldProductRepository : CrudRepository<SoldProductEntity, Int> {
}

@Repository
interface SubmittedAchievementRepository : CrudRepository<SubmittedAchievementEntity, Int> {
}

@Repository
interface RealtimeConfigRepository : CrudRepository<RealtimeConfigEntity, Int> {

    fun findByKey(key: String): Optional<RealtimeConfigEntity>

}