package hu.bme.sch.cmsch.component.script

import hu.bme.sch.cmsch.component.ComponentApiBase
import hu.bme.sch.cmsch.component.app.MenuService
import hu.bme.sch.cmsch.service.AdminMenuService
import hu.bme.sch.cmsch.service.AuditLogService
import hu.bme.sch.cmsch.service.ControlPermissions.PERMISSION_CONTROL_SCRIPT
import hu.bme.sch.cmsch.service.StorageService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/admin/control/component/script")
@ConditionalOnBean(ScriptComponent::class)
class ScriptComponentController(
    adminMenuService: AdminMenuService,
    component: ScriptComponent,
    menuService: MenuService,
    auditLogService: AuditLogService,
    storageService: StorageService
) : ComponentApiBase(
    adminMenuService,
    ScriptComponent::class.java,
    component,
    PERMISSION_CONTROL_SCRIPT,
    "Scriptek",
    "Scriptek testreszabása",
    menuService = menuService,
    auditLogService = auditLogService,
    storageService = storageService,
    documentationMenuName = "Script docs",
    documentationMarkdown = $$"""
    
    # Alapok
    
    Alapból be van injektálva néhány változó, amit fogsz tudni használni a kód írásakor. 
    Ezek a `context`, `json`, `csv`. A kódot a szerver futtatja. Csak olyannak adj jogot módosítani 
    scriptet akinek admint is adnál.
    
    Futtatni megírt scriptet lehet alacsonyabb jogosultságokkal. Jelenleg nem lehet egyesével állítani 
    jogosultságokat scriptekhez.
    
    A konzolra kiírást a contexten keresztül csináld, a sima `println` nem ide fogja kiírni. A közzétett
    fájlokat meg tudod tekinteni az appban is, és le is tudod tölteni. Ha nagyon nagy fájlt csinálsz, 
    az lehet hogy belassítja a dolgokat. Lehetne bőven optimalizálni a kódot.
    
    # Read-only
    
    A scriptedet tudod read-only és módosító módban indítani. A read-only nem teljesen gátolja meg, hogy
    módosítsd a szerver állapotát, de véletlenül valszeg nem fogsz elrontani semmit.
    Nincs rá külön permission, mert ugyan úgy veszélyes lehet.
    
    # Context
       
    Ebben az osztályban van a legtöbb segédfüggévény.
    
    | Signature                                            | Leírás                                                     |
    | ---------------------------------------------------- | ---------------------------------------------------------- |
    | `.info(message: String)`                             | Normál kiírás                                              |
    | `.info(message: Any?)`                               | Normál kiírás, a toString()-et hívja meg                   |
    | `.println(message: String)`                          | Alias az infohoz                                           |
    | `.warn(message: String)`                             | Warning kiírás                                             |
    | `.warn(message: Any?)`                               | Warning kiírás, a toString()-et hívja meg                  |
    | `.error(message: String)`                            | Hiba kiírás                                                |
    | `.error(message: Any?)`                              | Hiba kiírás, a toString()-et hívja meg                     |
    | `.error(message: String?, exception: Exception?)`    | Hiba kiírás, az exception nevét is kiírja                  |
    | `.printStackTrace(exception: Exception?)`            | Kiírja a teljes hívási láncot                              |
    | `.debug(message: String) `                           | Csak hiba javításhoz, alapból nem látszik                  |
    | `.debug(message: Any?)`                              | Csak hiba javításhoz, alapból nem látszik                  |
    | `.publishArtifact(artifact: ScriptArtifact)`         | Fájl mentése a futáshoz                                    |
    | `.readOnlyDb.repository(XyRepository::class)`        | Egy factory olyan repókhoz amik nem módosítanak a DB-ben   |
    | `.modifyingDb.repository(XyRepository::class)`       | Egy factory olyan repókhoz amik tudnak módosítani a DB-ben |
    | `.modifyingComponents.component(XyComponent::class)` | Factory az adott komponens konfigurációjához               |
    
    A `readOnlyDb` alatt csak a `findAll(): List<T>`, `findById(id: ID): T?` és a `findAllById(id: List<ID>): List<T>` 
    érhető el. A `modifyingDb` alatt a hivatkozott repó minden függvénye elérhető.
    
    # Csv
    
    A ˙csv˙ alatt CSV stringet tudszt generálni: külső lista sorok, belső lista oszlopok megadásával
    
    ```kotlin
        fun generateCsv(
            content: List<List<String>>,
            columnSeperator: Char = ',',
            lineSeparator: String = "\n",
            quoteChar: Char = '"'
        ): String { ... }
        
        // használat:
        
        csv.generateCsv(listOf(
            listOf("A1", "B1", "C1"),
            listOf("A2", "B2", "C2"),
        ))
    ```
    
    vagy ha van egy típusok hozzá, akkor pedig ezzel a függvénnyel:
    
    ```kotlin
        inline fun <reified T> generateTypedCsv(
            content: List<T>,
            columnSeperator: Char = ',',
            lineSeparator: String = "\n",
            quoteChar: Char = '"'
        ): String
        
        // használat:
        
        data class MyDto(
            val a: String,
            val b: String,
            val c: String,
        )
        csv.generateTypedCsv(listOf(
            MyDto("A1", "B1", "C1"),
            MyDto("A2", "B2", "C2"),
        ))
    ```
    
    # Json
    
    Hasonló mint a `csv`, csak JSON generálásra. Ez a két függvéány érhető el: `json.generatePrettyJson(value: Any)` és
    `json.generateJson(value: Any)`. A Pretty tesz bele sortöréseket és betördeli. A sima egybe írja kompaktra.
       
    # Maven dependency importálása
    
    Tedd ezt a script tetejére, és akkor futtatáskor le fogja húzni és classpathra helyezi.
    
    ```kotlin
        @file:Repository("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        @file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    ```
    
    # Nem találja a típusokat?
    
    Lehet, hogy be kell importálnod őket. Egy csomó package automatikusan be van importálva, de nem az összes.
        
    # Példa kódok
    
    #### Logok használata
    
    ```kotlin
        context.debug("Ez a szöveg nem fog látszódni alapból!");
        context.debug("Ehhez használd: context.debug(\"\")");
        Thread.sleep(1500); // Hogy látható legyen a live sync
        
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
    ```
    
    #### Összes user nevének listázása
    
    ```kotlin
        context.info("Users:")
        context.readOnlyDb.repository(UserRepository::class).findAll().forEach { user ->
            context.info("- ${user.fullName}")
        }
    ```
    
    ```kotlin
        context.info("Users:")
        context.modifyingDb.repository(UserRepository::class).findAll().forEach { user ->
            context.info("- ${user.fullName}")
        }
    ```
    
    #### Fájlok publikálása
    
    ```
        data class UserView(
            val fullName: String,
            val nickname: String,
            val email: String,
        )
        
        val result = context.readOnlyDb.repository(UserRepository::class).findAll().map {
           user -> UserView(user.fullName, user.nickname, user.email)
        }
        result.forEach {
            context.info(it)
        }
        
        val contentCsv = csv.generateTypedCsv(result)
        context.publishArtifact(ScriptArtifact(
            fileName = "users-report.csv",
            artifactName = "Users table",
            type = "text/csv",
            content = contentCsv,
        ))
        val contentJson = json.generatePrettyJson(result)
        context.publishArtifact(ScriptArtifact(
            fileName = "users-report.json",
            artifactName = "Users json",
            type = "application/json",
            content = contentJson,
        ))
    ```
    
    #### Component konfigjának olvasása
    
    ```kotlin
        context.modifyingComponents.component(TokenComponent::class).reportTitle
    ```
    
    #### Maven import
    
    ```kotlin
        @file:Repository("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        @file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.3")
    
        import kotlinx.html.*
        import kotlinx.html.stream.*
        import kotlinx.html.attributes.*
        import hu.bme.sch.cmsch.repository.UserRepository
    
        context.info(
            createHTML().html {
                body {
                    h1 { +"Hello World!" }
                }
            }
        )
    ```
    
    #### Van fasza példa kódod? 
   
    Írd ide a `ScriptComponentController.kt`-ban. Köszi!
    
    # Hogyan álljak neki kódot írni?
    
    A legegyszerűbb ha lecloneozod a repót, megkeresed az IDE.kt fájl, és abban írod a scripteket, 
    mert úgy az IDE-d segít jó szintacissal írni a scriptet.
        
    """.trimIndent()
)
