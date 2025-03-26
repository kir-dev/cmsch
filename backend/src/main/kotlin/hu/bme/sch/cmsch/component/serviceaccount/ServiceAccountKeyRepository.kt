package hu.bme.sch.cmsch.component.serviceaccount

import hu.bme.sch.cmsch.model.UserEntity
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface ServiceAccountKeyRepository : CrudRepository<ServiceAccountKeyEntity, Int>,
    EntityPageDataSource<ServiceAccountKeyEntity, Int> {

    @Query("select u from ServiceAccountKeyEntity k inner join UserEntity u on k.userId = u.id and k.validUntil >= :now and k.secretKey = :secretKey")
    fun findUserByValidServiceAccountKey(secretKey: String, now: Long): UserEntity?

}
