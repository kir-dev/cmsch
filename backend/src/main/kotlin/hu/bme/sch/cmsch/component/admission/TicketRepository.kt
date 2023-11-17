package hu.bme.sch.cmsch.component.admission

import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository : JpaRepository<TicketEntity, Int>,
    EntityPageDataSource<TicketEntity, Int> {

    fun findTop1ByQrCodeAndUseCmschIdTrue(cmschId: String): List<TicketEntity>

    fun findTop1ByEmailAndUseCmschIdFalse(email: String): List<TicketEntity>

}