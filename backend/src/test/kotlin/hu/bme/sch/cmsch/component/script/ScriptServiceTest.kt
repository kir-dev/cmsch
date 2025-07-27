package hu.bme.sch.cmsch.component.script

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test", "internal")
class ScriptServiceTest {

    @Autowired
    lateinit var scriptService: ScriptService

    @Test
    fun testScript() {
        scriptService.executeScript(ScriptEntity(
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

                //println(context.modifyingDb.repository(UserRepository::class).findAll())
                //println(context.readOnlyDb.repository(UserRepository::class).findAll())

            """.trimIndent(),
            readOnly = false,
            description = "",
            components = "",
        ))
    }

    @Test
    fun testScriptMavenDependency() {
        scriptService.executeScript(ScriptEntity(
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
            components = "",
        ))
    }
}