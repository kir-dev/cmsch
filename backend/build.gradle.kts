import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.2.0"
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.spring") version "2.3.0"
    id("org.sonarqube") version "7.2.2.6593"
}

group = "hu.bme.sch"

// Set release version from the git tag when running in the workflow for the tag
// https://docs.github.com/en/actions/writing-workflows/choosing-what-your-workflow-does/store-information-in-variables#default-environment-variables
val ghRef: String? = System.getenv("GITHUB_REF")
version = if (ghRef != null && ghRef.startsWith("refs/tags/v")) ghRef.substring(11) else "dev"

java.sourceCompatibility = JavaVersion.VERSION_25

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
    implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
    implementation("com.github.spullara.mustache.java:compiler:0.9.14")
    implementation("com.google.firebase:firebase-admin:9.7.1") {
        exclude(module = "google-cloud-firestore")
        exclude(module = "google-cloud-storage")
    }
    implementation("com.google.zxing:core:3.5.4")
    implementation("com.google.zxing:javase:3.5.4")
    implementation("com.itextpdf:itext-core:9.5.0")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation(platform("io.jsonwebtoken:jjwt-bom:0.13.0"))
    runtimeOnly("io.jsonwebtoken:jjwt-impl")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson")
    implementation("io.jsonwebtoken:jjwt-api")
    implementation(platform("io.micrometer:micrometer-bom:1.16.2"))
    runtimeOnly("io.micrometer:micrometer-core")
    runtimeOnly("io.micrometer:micrometer-observation")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.27.1")
    implementation("org.commonmark:commonmark:0.27.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webclient")
    implementation("org.springframework.session:spring-session-jdbc")
    implementation("software.amazon.awssdk:s3:2.41.19")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

dependencyCheck {
    failBuildOnCVSS = 7f
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3)
        jvmTarget.set(JvmTarget.JVM_25)
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    builder = "paketobuildpacks/builder-jammy-tiny:latest"

    // FIXME: if you find a way to do this automatically, that would be nice :)
    val jlinkModules = "java.base,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.sql.rowset,jdk.compiler,jdk.jfr,jdk.management,jdk.net,jdk.unsupported,jdk.charsets"
    environment = mapOf(
        "BP_JVM_JLINK_ENABLED" to "true",
        "BP_JVM_JLINK_ARGS" to "--no-man-pages --no-header-files --strip-debug --compress=1 --add-modules $jlinkModules",
        "BPL_JVM_THREAD_COUNT" to "10",
        "BP_JVM_VERSION" to "25",
        "BPE_APPEND_JAVA_TOOL_OPTIONS" to "-Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8",
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
}
