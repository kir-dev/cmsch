package hu.bme.sch.cmsch.repository

import java.util.*

interface EntityPageDataSource<T, ID> {

    fun findAll(): Iterable<T>

    fun findById(id: ID): Optional<T>

    fun save(entity: T)

    fun delete(entity: T)

    fun count(): Long

    fun saveAll(entities: List<T>)

    fun deleteAll()

}