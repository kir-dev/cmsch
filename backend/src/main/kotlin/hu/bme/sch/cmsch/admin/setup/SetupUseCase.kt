package hu.bme.sch.cmsch.admin.setup

data class SetupUseCase(
    val name: String,
    val description: String,
    val tags: List<String>,
    val components: List<SetupComponent>,
)
