package hu.bme.sch.cmsch.repository

import java.lang.UnsupportedOperationException
import java.util.*

abstract class ManualRepository<T, ID> : EntityPageDataSource<T, ID> {

    override fun findAll(): Iterable<T> =
        throw UnsupportedOperationException("This data source does not support: findAll()")

    override fun count(): Long =
        findAll().count().toLong()

    override fun deleteAll(): Unit =
        throw UnsupportedOperationException("This data source does not support: deleteAll()")

    override fun <S : T?> saveAll(entities: Iterable<S>): Iterable<S> =
        throw UnsupportedOperationException("This data source does not support: saveAll(entities)")

    override fun <S : T & Any> save(entity: S): S =
        throw UnsupportedOperationException("This data source does not support: save(entity)")

    override fun delete(entity: T): Unit =
        throw UnsupportedOperationException("This data source does not support: delete(entity)")

    override fun findById(id: ID): Optional<T> =
        throw UnsupportedOperationException("This data source does not support: findById(id)")

}