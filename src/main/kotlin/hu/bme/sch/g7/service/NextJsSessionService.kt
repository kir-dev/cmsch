package hu.bme.sch.g7.service

import hu.bme.sch.g7.model.UserEntity
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

//@Service
@Deprecated("This service was used for G7 2021, but unused since then")
class NextJsSessionService {

    private val storage = ConcurrentHashMap<String, UserEntity>()
    private val random = SecureRandom()

    private fun generateNewId(): String {
        var id = ""
        for (x in 0..32)
            id += CHARS[random.nextInt(CHARS.length)]
        return id
    }

    fun storeUser(entity: UserEntity): String {
        val id = generateNewId()
        storage.entries.forEach { (token, storedEntity) -> if (storedEntity == entity) storage.remove(token) }
        storage[id] = entity
        entity.token = id
        return id
    }

    fun invalidate(id: String) {
        storage.remove(id)
    }

    fun getUser(id: String): UserEntity? {
        return storage.getOrDefault(id, null)
    }

}
