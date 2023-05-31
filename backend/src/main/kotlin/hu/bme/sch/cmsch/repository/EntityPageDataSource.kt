package hu.bme.sch.cmsch.repository

import java.util.*

interface EntityPageDataSource<T, ID> {

    fun findAll(): Iterable<T>

    fun findById(id: ID): Optional<T>

    fun delete(entity: T)

    fun count(): Long

    fun <S : T & Any> save(entity: S): S

    fun <S : T?> saveAll(entities: Iterable<S>): Iterable<S>

    fun deleteAll()

}