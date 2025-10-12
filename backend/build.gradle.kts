import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "4.0.0-M3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.1.6"
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    id("org.sonarqube") version "6.3.1.5724"
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
    implementation("com.google.firebase:firebase-admin:9.7.0")
    implementation("software.amazon.awssdk:s3:2.35.5")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.4")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:5.2.1")
    implementation("com.itextpdf:itext-core:9.3.0")
    implementation("com.github.spullara.mustache.java:compiler:0.9.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0-M1")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation(platform("io.jsonwebtoken:jjwt-bom:0.13.0"))
    implementation("io.jsonwebtoken:jjwt-api")
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.1")
    implementation("org.commonmark:commonmark:0.26.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.26.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    implementation("org.postgresql:postgresql")
    implementation(platform("io.micrometer:micrometer-bom:1.15.4"))
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-observation")
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
