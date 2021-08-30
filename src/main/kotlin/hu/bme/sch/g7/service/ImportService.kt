package hu.bme.sch.g7.service

import hu.bme.sch.g7.admin.*
import hu.bme.sch.g7.model.*
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

@Service
open class ImportService {

    private val mappings = mapOf<KClass<*>, (String) -> Enum<*>> (
            RoleType::class to ({ RoleType.valueOf(it) }),
            MajorType::class to ({ MajorType.valueOf(it) }),
            GuildType::class to ({ GuildType.valueOf(it) }),
            ProductType::class to ({ ProductType.valueOf(it) }),
            AchievementType::class to ({ AchievementType.valueOf(it) }),
    )

    @Transactional(rollbackFor = [Throwable::class])
    open fun <T> importEntities(
            repo: CrudRepository<T, Int>,
            raw: List<List<String>>,
            supplier: Supplier<T>,
            importModifiers: List<Pair<KProperty1<out Any, *>, ImportFormat>>
    ) {
        repo.saveAll(raw
                .filter { it.size > 1 }
                .map { dto ->
                    val entity = supplier.get()!!

                    importModifiers.forEach {
                        if (it.first is KMutableProperty1<out Any, *> && !it.second.ignore) {
                            when (it.second.type) {
                                IMPORT_RAW_TEXT -> {
                                    try {
                                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, dto[it.second.columnId])
                                    } catch (e: IllegalArgumentException) {
                                        println("Invalid field ${it.first.name} as type ${it.second.type} (TEXT) with value ${dto[it.second.columnId]}")
                                        throw e
                                    }
                                }
                                IMPORT_BOOLEAN -> {
                                    try {
                                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, dto[it.second.columnId].equals("true", ignoreCase = true))
                                    } catch (e: IllegalArgumentException) {
                                        println("Invalid field ${it.first.name} as type ${it.second.type} (BOOL) with value ${dto[it.second.columnId]}")
                                        throw e
                                    }
                                }
                                IMPORT_ENUM -> {
                                    try {
                                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, mappings[it.second.enumSource]?.invoke(dto[it.second.columnId])!!)
                                    } catch (e: IllegalArgumentException) {
                                        println("Invalid field ${it.first.name} as type ${it.second.type} (ENUM) with value ${dto[it.second.columnId]}")
                                        throw e
                                    }
                                }
                                IMPORT_LONG -> {
                                    try {
                                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, dto[it.second.columnId].toLong())
                                    } catch (e: IllegalArgumentException) {
                                        println("Invalid field ${it.first.name} as type ${it.second.type} (LONG) with value ${dto[it.second.columnId]}")
                                        throw e
                                    }
                                }
                                IMPORT_INT -> {
                                    try {
                                        (it.first as KMutableProperty1<out Any, *>).setter.call(entity, dto[it.second.columnId].toInt())
                                    } catch (e: IllegalArgumentException) {
                                        println("Invalid field ${it.first.name} as type ${it.second.type} (INT) with value ${dto[it.second.columnId]}")
                                        throw e
                                    }
                                }
                            }
                        }
                    }

                    return@map entity
                })

    }

}