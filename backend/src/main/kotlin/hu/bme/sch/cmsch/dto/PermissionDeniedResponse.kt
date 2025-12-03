package hu.bme.sch.cmsch.dto

import hu.bme.sch.cmsch.component.login.CmschUser

data class PermissionDeniedResponse(val permission: String, val user: CmschUser)
