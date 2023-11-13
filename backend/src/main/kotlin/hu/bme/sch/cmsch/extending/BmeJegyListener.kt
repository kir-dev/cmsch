package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.component.bmejegy.BmejegyRecordEntity
import hu.bme.sch.cmsch.model.UserEntity

interface BmeJegyListener {

    fun onTicketAdded(ticket: BmejegyRecordEntity)

    fun onTicketAssigned(userEntity: UserEntity, ticket: BmejegyRecordEntity)

    fun onTicketRaw(cell: Map<String, String>?)

}