package hu.bme.sch.cmsch.component.app

import hu.bme.sch.cmsch.model.RoleType
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@ConditionalOnBean(ApplicationComponent::class)
interface MenuRepository : CrudRepository<MenuEntity, Int> {

    @Query("select m from MenuEntity m where m.role = ?1")
    fun findAllByRole(role: RoleType): List<MenuEntity>

    @Modifying
    @Transactional
    @Query("delete from MenuEntity m where m.role = ?1")
    fun deleteAllByRole(role: RoleType)

}
