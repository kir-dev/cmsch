package hu.bme.sch.cmsch.component

import org.springframework.data.repository.CrudRepository as CrudRepository

interface RealEntityController {

    val repo: CrudRepository<*, Int>

}