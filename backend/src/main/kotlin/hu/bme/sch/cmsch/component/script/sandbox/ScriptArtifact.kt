package hu.bme.sch.cmsch.component.script.sandbox

data class ScriptArtifact(
    val fileName: String,
    val artifactName: String,
    val type: String,
    val content: String,
)