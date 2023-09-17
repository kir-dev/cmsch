package hu.bme.sch.cmsch.component.qrfight

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
@ConditionalOnBean(QrFightComponent::class)
interface QrTowerRepository : CrudRepository<QrTowerEntity, Int>,
    EntityPageDataSource<QrTowerEntity, Int> {

    override fun findAll(): List<QrTowerEntity>
    fun findAllBySelector(selector: String): List<QrTowerEntity>
    fun findAllByCategory(category: String): List<QrTowerEntity>
    fun findAllByRecordTimeTrue(): List<QrTowerEntity>
    fun countAllByOwnerGroupId(groupId: Int): Int
}
