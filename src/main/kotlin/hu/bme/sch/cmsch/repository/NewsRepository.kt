package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.NewsEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface NewsRepository : CrudRepository<NewsEntity, Int> {
    fun findTop4ByVisibleTrueOrderByTimestampDesc(): List<NewsEntity>
    fun findAllByVisibleTrueOrderByTimestampDesc(): List<NewsEntity>
}