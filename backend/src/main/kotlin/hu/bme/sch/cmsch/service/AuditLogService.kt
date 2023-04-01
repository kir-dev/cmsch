package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.login.CmschUser
import org.springframework.stereotype.Service

@Service
class AuditLogService {

    fun access(user: CmschUser, component: String, action: String) {

    }

    fun edit(user: CmschUser, component: String, action: String) {

    }

    fun delete(user: CmschUser, component: String, action: String) {

    }

    fun important(user: CmschUser, component: String, action: String) {

    }

}