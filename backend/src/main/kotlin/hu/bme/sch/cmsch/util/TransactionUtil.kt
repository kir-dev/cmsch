package hu.bme.sch.cmsch.util

import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.DefaultTransactionDefinition

inline fun <T> PlatformTransactionManager.transaction(
    readOnly: Boolean = true,
    isolation: Int = TransactionDefinition.ISOLATION_READ_COMMITTED,
    propagation: Int = TransactionDefinition.PROPAGATION_REQUIRED,
    action: (TransactionStatus) -> T
): T {
    val transactionStatus = this.getTransaction(DefaultTransactionDefinition().apply {
        this.isReadOnly = readOnly
        this.isolationLevel = isolation
        this.propagationBehavior = propagation
    })
    return try {
        val result = action(transactionStatus)
        this.commit(transactionStatus)
        result
    } catch (e: Exception) {
        this.rollback(transactionStatus)
        throw e
    }
}
