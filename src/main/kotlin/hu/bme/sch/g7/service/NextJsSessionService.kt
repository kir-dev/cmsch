package hu.bme.sch.g7.service

import hu.bme.sch.g7.model.UserEntity
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

@Service
class NextJsSessionService {

    private val storage = ConcurrentHashMap<String, UserEntity>()
    private val random = SecureRandom()

    private fun generateNewId(): String {
        var id = ""
        for (x in 0..64)
            id += CHARS[random.nextInt(CHARS.length)]
        return id
    }

    fun storeUser(entity: UserEntity): String {
        val id = generateNewId()
        storage.entries.forEach { (token, storedEntity) -> if (storedEntity.equals(entity)) storage.remove(token) }
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