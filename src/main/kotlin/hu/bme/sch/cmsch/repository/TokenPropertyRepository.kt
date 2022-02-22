package hu.bme.sch.cmsch.repository

import hu.bme.sch.cmsch.model.GroupEntity
import hu.bme.sch.cmsch.model.TokenPropertyEntity
import hu.bme.sch.cmsch.model.UserEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TokenPropertyRepository : CrudRepository<TokenPropertyEntity, Int> {

    override fun findAll(): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerUser_Id(owner: Int): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByToken_Id(token: Int): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerUser_IdAndToken_Type(owner: Int, type: String): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findAllByOwnerGroup_Id(owner: Int): List<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByToken_TokenAndOwnerUser(token: String, owner: UserEntity): Optional<TokenPropertyEntity>

    @Suppress("FunctionName", "kotlin:S100") // This is the valid naming conversion of spring-data
    fun findByToken_TokenAndOwnerGroup(token: String, owner: GroupEntity): Optional<TokenPropertyEntity>

}