package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.ExtraPageEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ExtraPageRepository : CrudRepository<ExtraPageEntity, Int> {
    fun findByUrlAndVisibleTrue(path: String): Optional<ExtraPageEntity>
}