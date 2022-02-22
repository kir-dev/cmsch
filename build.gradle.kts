import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.yarn.task.YarnTask
import java.util.Properties

plugins {
    id("org.springframework.boot") version "2.6.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.node-gradle.node") version "3.1.1"
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
}

val applicationProperties = Properties()
file("${projectDir}/src/main/resources/config/application.properties")
    .reader()
    .also {
        applicationProperties.load(it)
    }
group = "hu.bme.sch"
version = "2.6.5"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks {
    bootJar {
        archiveFileName.set("cmsch.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.itextpdf:itext7-core:7.2.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    implementation("mysql:mysql-connector-java")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")
    implementation("com.google.zxing:core:3.3.0")
    implementation("com.google.zxing:javase:3.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

node {
    version.set("16.13.2")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.gradle/npm"))
    yarnWorkDir.set(file("${project.projectDir}/.gradle/yarn"))
    nodeProjectDir.set(file("${project.projectDir}/src/main/client"))
}

tasks.withType<ProcessResources> {
    dependsOn("copyFrontendToBuild")
}

tasks.register<Copy>("copyFrontendToBuildFast") {
    dependsOn("yarnBuildNoInstall")
    from("$projectDir/src/main/client/build/")
    into("$buildDir/resources/main/static")
}

tasks.register<YarnTask>("yarnBuildNoInstall") {
    yarnCommand.set(listOf("run", "build"))
    workingDir.set(file("src/main/client"))
    inputs.dir("src")
    outputs.dir("$buildDir")
}

tasks.register<Copy>("copyFrontendToBuild") {
    dependsOn("yarnBuild")
    from("$projectDir/src/main/client/build/")
    into("$buildDir/resources/main/static")
}

tasks.register<YarnTask>("yarnBuild") {
    dependsOn(tasks.yarn, "yarnFormatEslint", "yarnFormatPrettier", "setupBuildEnv")
    yarnCommand.set(listOf("run", "build"))
    workingDir.set(file("src/main/client"))
    inputs.dir("src")
    outputs.dir("$buildDir")
}

tasks.register<YarnTask>("yarnFormatEslint") {
    yarnCommand.set(listOf("run", "fix:eslint"))
    workingDir.set(file("src/main/client"))
    inputs.dir("src")
}

tasks.register<YarnTask>("yarnFormatPrettier") {
    yarnCommand.set(listOf("run", "fix:prettier"))
    workingDir.set(file("src/main/client"))
    inputs.dir("src")
}

tasks.register("setupBuildEnv") {
    doLast {
        File("$projectDir/src/main/client", ".env").writeText(
            "REACT_APP_BACKEND_BASE_URL=${applicationProperties.getProperty("cmsch.backend.production-url")}" +
            "\nREACT_APP_KIRDEV_URL=${applicationProperties.getProperty("cmsch.frontend.kirdev-url")}" +
            "\nREACT_APP_BUGREPORT_URL=${applicationProperties.getProperty("cmsch.frontend.bugreport-url")}" +
            "\nREACT_APP_NAME=${applicationProperties.getProperty("cmsch.frontend.appname")}"
        )
    }
}

tasks.yarn {
    nodeModulesOutputFilter {
        exclude("notExistingFile")
    }
    workingDir.set(file("src/main/client"))
}
