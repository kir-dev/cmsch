import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.gradle.node.yarn.task.YarnTask

plugins {
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.github.node-gradle.node") version "3.1.1"
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.spring") version "1.5.20"
    kotlin("plugin.jpa") version "1.5.20"
}

group = "hu.bme.sch"
version = "1.0.13"
java.sourceCompatibility = JavaVersion.VERSION_11

tasks {
    bootJar {
        archiveFileName.set("g7.jar")
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
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

node {
    version.set("16.13.2")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(false)
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.gradle/npm"))
    yarnWorkDir.set(file("${project.projectDir}/.gradle/yarn"))
    nodeProjectDir.set(file("${project.projectDir}/src/main/client"))
}

tasks.withType<ProcessResources> {
    dependsOn("copyFrontendToBuild")
}

tasks.register<Copy>("copyFrontendToBuild") {
    from("$projectDir/src/main/client/build/")
    into("$buildDir/resources/main/static")
}

val backendBaseUrl = "https://gkorte.sch.bme.hu/"

tasks.register<YarnTask>("yarnBuild") {
    dependsOn(tasks.yarn, "setupBuildEnv")
    yarnCommand.set(listOf("run", "build"))
    workingDir.set(file("src/main/client"))
    inputs.dir("src")
    outputs.dir("$buildDir")
}

tasks.register("setupBuildEnv") {
    doLast {
        File("$projectDir/src/main/client", ".env")
            .writeText("REACT_APP_BACKEND_BASE_URL=$backendBaseUrl")
    }
}

tasks.yarn {
    nodeModulesOutputFilter {
        exclude("notExistingFile")
    }
    workingDir.set(file("src/main/client"))
}
