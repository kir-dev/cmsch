package hu.bme.sch.cmsch.component.script

import kotlin.script.experimental.api.ScriptDiagnostic.Severity

data class ScriptLogLineDto(
    val message: String,
    val severity: Severity = Severity.ERROR,
    val exception: String? = null
)