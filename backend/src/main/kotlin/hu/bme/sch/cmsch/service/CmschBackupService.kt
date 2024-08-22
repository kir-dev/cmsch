package hu.bme.sch.cmsch.service

import hu.bme.sch.cmsch.component.RealEntityController
import org.springframework.stereotype.Service

@Service
class CmschBackupService(
    private val realEntities: List<RealEntityController>
) {



}