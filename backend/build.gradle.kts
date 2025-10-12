import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.1.9"
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.sonarqube") version "7.1.0.6387"
}

group = "hu.bme.sch"

// Set release version from the git tag when running in the workflow for the tag
// https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/store-information-in-variables#default-environment-variables
val ghRef: String? = System.getenv("GITHUB_REF")
version = if (ghRef != null && ghRef.startsWith("refs/tags/v")) ghRef.substring(11) else "dev"

java.sourceCompatibility = JavaVersion.VERSION_24

springBoot {
    buildInfo()
}

tasks {
    bootJar {
        archiveFileName.set("cmsch.jar")
    }
}

sonar {
    properties {
        property("sonar.projectKey", "kir-dev_cmsch-backend")
        property("sonar.organization", "kir-dev")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("tools.jackson.dataformat:jackson-dataformat-csv")
    implementation("tools.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.1")
    implementation("com.github.spullara.mustache.java:compiler:0.9.14")
    implementation("com.google.firebase:firebase-admin:9.7.0")
    implementation("com.google.zxing:core:3.5.4")
    implementation("com.google.zxing:javase:3.5.4")
    implementation("com.itextpdf:itext-core:9.4.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation(platform("io.jsonwebtoken:jjwt-bom:0.13.0"))
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    implementation("io.jsonwebtoken:jjwt-api")
    implementation(platform("io.micrometer:micrometer-bom:1.16.0"))
    runtimeOnly("io.micrometer:micrometer-core")
    runtimeOnly("io.micrometer:micrometer-observation")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.27.0")
    implementation("org.commonmark:commonmark:0.27.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("software.amazon.awssdk:s3:2.39.3")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("org.springframework.security:spring-security-test")
}

dependencyCheck {
    failBuildOnCVSS = 7f
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        jvmTarget.set(JvmTarget.JVM_24)
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    buildpacks = listOf("docker.io/paketobuildpacks/adoptium", "urn:cnb:builder:paketo-buildpacks/java")
    builder = "paketobuildpacks/builder-jammy-base"
    environment = mapOf(
        "BP_NATIVE_IMAGE" to "false",
        "CDS_TRAINING_JAVA_TOOL_OPTIONS" to "-Dspring.profiles.include=prewarm",
        "BP_JVM_CDS_ENABLED" to "true",
        "BPL_JVM_CDS_ENABLED" to "true",
        "BPL_JVM_THREAD_COUNT" to "25"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
