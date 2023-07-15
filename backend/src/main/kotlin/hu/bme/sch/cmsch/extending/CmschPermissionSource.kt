package hu.bme.sch.cmsch.extending

import hu.bme.sch.cmsch.service.PermissionValidator

interface CmschPermissionSource {

    fun getControlPermissions(): List<PermissionValidator> = listOf()

    fun getStaffPermissions(): List<PermissionValidator> = listOf()

}