package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.model.UserEntity
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("""
    This one is for testing the scrips locally. Not real scripts.
    Don't commit changes.
""")
@SpringBootTest
@ActiveProfiles("test")
class ScriptServiceManualTest {

    @Autowired
    lateinit var scriptService: ScriptService

    @Test
    fun testScript() {
        val user = UserEntity(id = 69, fullName = "Test User")
        scriptService.executeScript(user, ScriptEntity(
            name = "testScript",
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

                println("Hello world")
    
                data class TestDto(
                    val a: String,
                    val testB: String,
                )
                val listOf = listOf(TestDto("asdasd", "b"), TestDto("asdasdcccc", "c"))
                println(json.generatePrettyJson(listOf))
                println(csv.generateTypedCsv(listOf))
                
                println(context.modifyingComponents.component(TokenComponent::class).reportTitle)

                //println(context.modifyingDb.repository(UserRepository::class).findAll())
                //println(context.readOnlyDb.repository(UserRepository::class).findAll())

            """.trimIndent(),
            readOnly = false,
            description = "",
            entities = "*",
        )).second.get()
    }

    @Test
    fun testScriptMavenDependency() {
        val user = UserEntity(id = 69, fullName = "Test User")
        scriptService.executeScript(user, ScriptEntity(
            name = "testScript",
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
            """.trimIndent(),
            readOnly = true,
            description = "",
            entities = "UserEntity",
        )).second.get()
    }
}