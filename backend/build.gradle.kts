import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.owasp.dependencycheck") version "9.2.0"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
}

group = "hu.bme.sch"
version = "4.6.0"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    bootJar {
        archiveFileName.set("cmsch.jar")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-oauth2-client")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api("org.springframework.boot:spring-boot-starter-actuator")
    api("org.springframework.retry:spring-retry")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.itextpdf:itext-core:8.0.4")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
    api("com.google.zxing:core:3.5.3")
    api("com.google.zxing:javase:3.5.3")
    api("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
    api("com.fasterxml.uuid:java-uuid-generator:5.0.0")
    api("org.commonmark:commonmark:0.22.0")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-csv")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    implementation("org.postgresql:postgresql")
    implementation(platform("io.micrometer:micrometer-bom:1.13.0"))
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
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

