import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.1.1"
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.spring") version "2.1.21"
    id("org.sonarqube") version "6.2.0.5505"
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
    implementation("com.google.firebase:firebase-admin:9.5.0")
    implementation("software.amazon.awssdk:s3:2.31.57")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
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
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.itextpdf:itext-core:9.2.0")
    implementation("com.github.spullara.mustache.java:compiler:0.9.14")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.1.0")
    implementation("org.commonmark:commonmark:0.24.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.24.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    implementation("org.postgresql:postgresql")
    implementation(platform("io.micrometer:micrometer-bom:1.15.0"))
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
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder = "bellsoft/buildpacks.builder:glibc"
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
