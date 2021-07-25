package hu.bme.sch.g7.dao

import hu.bme.sch.g7.model.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementRepository : CrudRepository<AchievementEntity, Int> {
}

@Repository
interface EventRepository : CrudRepository<EventEntity, Int> {
}

@Repository
interface GroupRepository : CrudRepository<GroupEntity, Int> {
}

@Repository
interface NewsRepository : CrudRepository<NewsEntity, Int> {
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
interface SubmittedAchievementRepository : CrudRepository<SubmittedAchivementEntity, Int> {
}
