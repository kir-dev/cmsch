package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.PermissionGroupEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionGroupRepository : CrudRepository<PermissionGroupEntity, Int>,
    EntityPageDataSource<PermissionGroupEntity, Int>  {
}