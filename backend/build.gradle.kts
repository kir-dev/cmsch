import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.owasp.dependencycheck") version "6.1.6"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"

}

group = "hu.bme.sch"
version = "4.0.0"
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
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    api("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-oauth2-client")
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-webflux")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.itextpdf:itext7-core:7.2.3")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.springdoc:springdoc-openapi-ui:1.7.0")
    api("org.springdoc:springdoc-openapi-security:1.7.0")
    api("org.springdoc:springdoc-openapi-kotlin:1.7.0")
    api("com.google.zxing:core:3.5.1")
    api("com.google.zxing:javase:3.5.1")
    api("io.jsonwebtoken:jjwt:0.9.1")
    api("com.fasterxml.uuid:java-uuid-generator:4.2.0")
    api("org.commonmark:commonmark:0.21.0")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.15.1")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    runtimeOnly("com.h2database:h2")
    testApi("org.springframework.boot:spring-boot-starter-test")
    testApi("org.springframework.security:spring-security-test")
}

dependencyCheck {
    failBuildOnCVSS = 7f
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

