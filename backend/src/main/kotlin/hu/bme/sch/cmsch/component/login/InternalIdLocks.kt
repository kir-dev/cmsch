package hu.bme.sch.cmsch.component.login

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

class InternalIdLocks {

    private val locks = ConcurrentHashMap<String, WeakReference<ReentrantLock>>()

    private fun getLockForKey(key: String): ReentrantLock {
        locks.entries.removeIf { it.value.get() == null }

        val newLockRef = WeakReference(ReentrantLock())
        val existingLockRef = locks.putIfAbsent(key, newLockRef)

        return existingLockRef?.get() ?: newLockRef.get()!!
    }

    fun lockForKey(key: String): ReentrantLock {
        val lockForKey = getLockForKey(key)
        lockForKey.lock()
        return lockForKey
    }

}