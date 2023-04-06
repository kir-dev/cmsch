package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(QrFightComponent::class)
interface QrLevelRepository : CrudRepository<QrLevelEntity, Int>,
    EntityPageDataSource<QrLevelEntity, Int> {

    override fun findAll(): List<QrLevelEntity>
    fun findAllByCategory(category: String): List<QrLevelEntity>
    fun findAllByVisibleTrueAndEnabledTrue(): List<QrLevelEntity>
}
