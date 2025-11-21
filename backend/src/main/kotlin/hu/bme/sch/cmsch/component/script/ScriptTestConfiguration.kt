package hu.bme.sch.cmsch.component.script

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import jakarta.annotation.PostConstruct

@Configuration
@ConditionalOnBean(ScriptComponent::class)
@Profile("test")
class ScriptTestConfiguration(
    private val scriptRepository: ScriptRepository,
    private val scriptResultRepository: ScriptResultRepository,
    private val scriptService: ScriptService
) {

    @PostConstruct
    fun seedData() {
        if (scriptRepository.findAll().toList().isNotEmpty())
            return

        scriptRepository.save(ScriptEntity(
            name = "Maven Example",
            script = """
                @file:Repository("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
                @file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
                
    
                import kotlinx.html.*
                import kotlinx.html.stream.*
                import kotlinx.html.attributes.*
                import hu.bme.sch.cmsch.repository.UserRepository
    
                val addressee = "World"
    
                print(
                    createHTML().html {
                        body {
                            h1 { +"Hello, !" }
                        }
                    }
                )
    
                context.info("Hello world")
    
                data class TestDto(
                    val a: String,
                    val testB: String,
                )
                
                val listOf = listOf(TestDto("asdasd", "b"), TestDto("asdasdcccc", "c"))
                
                context.info(json.generatePrettyJson(listOf))
                context.info(csv.generateTypedCsv(listOf))
    
                //println(context.modifyingDb.repository(UserRepository::class).findAll())
                //println(context.readOnlyDb.repository(UserRepository::class).findAll())
    
            """.trimIndent(),
            readOnly = false,
            description = "",
            entities = "*",
        ))

        scriptRepository.save(ScriptEntity(
            name = "Read Only Example",
            script = """
                val result = context.readOnlyDb.repository(UserRepository::class).findAll()
                result.forEach {
                    context.info(it)
                }
                
                val contentCsv = csv.generateTypedCsv(result)
                context.publishArtifact(ScriptArtifact(
                    fileName = "users-report.csv",
                    artifactName = "User table",
                    type = "text/csv",
                    content = content,
                ))
            """.trimIndent(),
            readOnly = true,
            description = "Kilistázza az összes felhasználót és készít egy CSV-t belőlük",
            entities = "UserEntity",
        ))

        scriptRepository.save(ScriptEntity(
            name = "Log example",
            script = """
                context.debug("Ez a szöveg nem fog látszódni alapból!");
                
                context.debug("Ehhez használd: context.debug(\"\")");
                
                Thread.sleep(1500);
                
                context.info("Ez a normál szöveg, ebből lesz a legtöbb!");
                
                Thread.sleep(1500);
                
                context.info("Lorem ipsum dolor sit amet!");
                
                context.info("Ehhez használd: context.info(\"\")");
                
                Thread.sleep(1500);
                
                context.warn("Ez ilyen figyelem felhívó!");
                
                context.warn("Ehhez használd: context.warn(\"\")");
                
                Thread.sleep(1500);
                
                context.error("Full gatya");
                
                context.error("Ehhez használd: context.error(\"\")");
                
            """.trimIndent(),
            readOnly = true,
            description = "Különböző logüzenetekre példa, és várakozások közben teszteléshez",
            entities = "",
        ))

        val modifyingExample = ScriptEntity(
            name = "Modifying DB Example",
            script = """
                import hu.bme.sch.cmsch.repository.UserRepository
                
                
                val result = context.modifyingDb.repository(UserRepository::class).findAll().take(10)
                
                
                result.forEach {
                    context.info(it)
                }
                
                
                val content = csv.generateTypedCsv(result)
                
                context.publishArtifact(ScriptArtifact(
                    fileName = "users-report.csv",
                    artifactName = "User table",
                    type = "text/csv",
                    content = content,
                ))
            """.trimIndent(),
            readOnly = false,
            description = "Kilistázza az összes felhasználót és készít egy CSV-t belőlük",
            entities = "UserEntity",
        )
        scriptRepository.save(modifyingExample)

        scriptResultRepository.save(ScriptResultEntity(
            userName = "system",
            scriptId = modifyingExample.id,
            success = true,
            running = false,
            duration = 200,
            logs = "[]",
            artifacts = "[]",
            timestamp = (System.currentTimeMillis() / 1000) - 40000,
        ))

        scriptResultRepository.save(ScriptResultEntity(
            userName = "system",
            scriptId = modifyingExample.id,
            success = false,
            running = false,
            duration = 200,
            logs = "[]",
            artifacts = "[]",
            timestamp = (System.currentTimeMillis() / 1000) - 30000,
        ))

        scriptResultRepository.save(ScriptResultEntity(
            userName = "system",
            scriptId = modifyingExample.id,
            success = false,
            running = true,
            duration = 0,
            logs = "[]",
            artifacts = "[]",
            timestamp = (System.currentTimeMillis() / 1000),
        ))

    }

}
