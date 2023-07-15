package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.admin.CsvParserUtil
import hu.bme.sch.cmsch.repository.EntityPageDataSource
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.InputStream
import kotlin.reflect.KClass

@Service
open class ImportService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(rollbackFor = [Throwable::class])
    open fun <T : Any> importEntities(
        repo: EntityPageDataSource<T, Int>,
        stream: InputStream,
        type: KClass<T>
    ) {
        try {
            val parser = CsvParserUtil(type)
            repo.saveAll(parser.importFromCsv(stream))
        } catch (e: Exception) {
            log.error("Failed to import", e)
            throw e
        }
    }

}
