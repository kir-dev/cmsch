package hu.bme.sch.cmsch.component.signup

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SignupFormRepository : JpaRepository<SignupFormEntity, Int> {
}
